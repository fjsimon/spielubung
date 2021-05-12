package org.fjsimon.spielubung.delegate;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fjsimon.spielubung.model.SpielMessage;
import org.fjsimon.spielubung.model.Spielzug;
import org.fjsimon.spielubung.model.Spieler;
import org.fjsimon.spielubung.expections.ApplicationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;
import static  org.fjsimon.spielubung.delegate.SpielMessageFactory.*;

@Slf4j
@Service
@AllArgsConstructor
public class SpielDelegate {

    private final SpielerRepository spielerRepository;
    private final MeldungService meldungService;

    public void save(Spieler spieler) {

        spielerRepository.save(spieler);
    }

    @Retryable(value = RuntimeException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public void remove(String spielerName) {

        spielerRepository.findByName(spielerName)
            .ifPresent(player -> {

                spielerRepository.delete(player);
                ofNullable(player.getGegenspieler())
                    .ifPresent(p -> {
                        meldungService.notifyPlayer(p.getName(),
                            aTrennenMessage(p.getGegenspieler().getName()));
                        p.setAvailable(true);
                        p.setGegenspieler(null);
                        spielerRepository.save(p);
                    });

            });
    }

    public SpielMessage start(String spielerName) {

        return spielerRepository.findByName(spielerName)
            .map(this::processStartRequestForPlayer)
            .orElseThrow(() -> new ApplicationException(String
                .format("Player not found: %s", spielerName)));
    }

    public void number(int randomNumber, String spielerName) {

        Spieler spieler = spielerRepository.findByName(spielerName)
            .orElseThrow(() ->  new ApplicationException(String.format("Player not found: %s", spielerName)));

        checkPlayerHasOpponent(spieler);
        meldungService.notifyPlayer(spieler.getGegenspieler().getName(), aSpielenMessage(randomNumber));
    }


    public void move(Spielzug spielzug, String spielerName) {

        int addition = spielzug.getValue() + spielzug.getMove();

        checkDivisibleByDivisor(addition);

        Spieler spieler =spielerRepository.findByName(spielerName)
            .orElseThrow(() -> new ApplicationException(String.format("Player not found: %s", spielerName)));

        checkPlayerHasOpponent(spieler);
        int newValue = addition / 3;
        log(spielerName, spielzug, newValue);
        if (newValue != 1) {
            meldungService.notifyPlayer(spieler.getGegenspieler().getName(), aSpielenMessage(newValue));
        } else {
            meldungService.notifyPlayer(spieler.getName(), aSpielEndeMessage(true));
            meldungService.notifyPlayer(spieler.getGegenspieler().getName(), aSpielEndeMessage(false));
        }
    }


    // PRIVATE
    private SpielMessage processStartRequestForPlayer(Spieler spieler) {

        return ofNullable(spieler.getGegenspieler())
                .map(this::rematchWithGegenspieler)
                .orElseGet(() -> pairPlayerWithAvailablePlayer(spieler)
                        .orElseGet(SpielMessageFactory::aWartenMessage));
    }

    private SpielMessage rematchWithGegenspieler(Spieler spieler) {

        meldungService.notifyPlayer(spieler.getName(), aStartenMessage(spieler));
        return aStartenMessage(spieler.getGegenspieler());
    }

    private Optional<SpielMessage> pairPlayerWithAvailablePlayer(Spieler spieler) {

        return spielerRepository.findByNameNotAndGegenspielerIsNull(spieler.getName())
            .map(availablePlayer -> {

                availablePlayer.setPrimary(true);
                spieler.setPrimary(false);

                availablePlayer.setGegenspieler(spieler);
                spieler.setGegenspieler(availablePlayer);

                availablePlayer.setAvailable(false);
                spieler.setAvailable(false);

                savePlayerChanges(availablePlayer);
                meldungService.notifyPlayer(availablePlayer.getName(), aStartenMessage(availablePlayer));

                return aStartenMessage(spieler);
            });
    }

    private void savePlayerChanges(Spieler spieler) {

        spielerRepository.save(spieler);
        ofNullable(spieler.getGegenspieler()).ifPresent(spielerRepository::save);
    }


    private void checkPlayerHasOpponent(Spieler spieler) {

        if (null == spieler.getGegenspieler()) {
            throw new ApplicationException("You have not been paired with an opponent");
        }
    }

    private void checkDivisibleByDivisor(int number) {

        if (number % 3 != 0) {
            throw new ApplicationException(String.format("%d is not divisible by %d", number, 3));
        }
    }

    private void log(String name, Spielzug spielzug, int updatedValue) {

        log.debug("Spieler {}, value {}, move {}, sum {}. Final value {}",
            name, spielzug.getValue(), spielzug.getMove(),
            spielzug.getValue() + spielzug.getMove(), updatedValue);
    }
}
