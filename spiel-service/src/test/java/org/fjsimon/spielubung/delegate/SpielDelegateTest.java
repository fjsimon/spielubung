package org.fjsimon.spielubung.delegate;

import org.fjsimon.spielubung.expections.ApplicationException;
import org.fjsimon.spielubung.model.Spieler;
import org.fjsimon.spielubung.model.Spielzug;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import nl.altindag.log.LogCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SpielDelegateTest {

    @Mock
    private SpielerRepository spielerRepository;

    @Mock
    private MeldungService meldungService;

    @InjectMocks
    private SpielDelegate testee;

    ArgumentCaptor<Spieler> spielerArgumentCaptor = ArgumentCaptor.forClass(Spieler.class);

    @Test
    public void save_should_call_repository_test() {

        Spieler spieler = new Spieler();
        spieler.setName("player");
        testee.save(spieler);

        verify(spielerRepository, times(1)).save(eq(spieler));
    }

    @Test
    public void remove_should_call_repository_test() {

        testee.remove("spieler");

        verify(spielerRepository, times(1)).findByName(eq("spieler"));
    }

    @Test
    public void remove_should_call_delegate_test() {

        Spieler spieler = new Spieler();
        spieler.setName("spieler");

        Spieler gegenspieler = new Spieler();
        gegenspieler.setName("gegenspieler");

        gegenspieler.setGegenspieler(spieler);
        spieler.setGegenspieler(gegenspieler);

        when(spielerRepository.findByName("spieler")).thenReturn(Optional.of(spieler));

        testee.remove("spieler");

        verify(meldungService, times(1)).notifyPlayer(eq("gegenspieler"), any());
        verify(spielerRepository, times(1)).findByName(eq("spieler"));
        verify(spielerRepository, times(1)).delete(any());
        verify(spielerRepository, times(1)).save(spielerArgumentCaptor.capture());

        Spieler spielerCaptorValue = spielerArgumentCaptor.getValue();
        assertThat(spielerCaptorValue.isAvailable(), is(true));
        assertThat(spielerCaptorValue.getGegenspieler(), is(nullValue()));
    }

    @Test()
    public void start_exception_test() {

        Assertions.assertThrows(ApplicationException.class, () -> {
            testee.start("spieler");
        });
    }

    @Test
    public void number_exception_test() {

        Assertions.assertThrows(ApplicationException.class, () -> {
            testee.number(9, "spieler");
        });
    }

    @Test
    public void move_wrong_values_exception_test() {

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            testee.move(Spielzug.builder().move(1).value(1).build(), "spieler");
        });

        assertThat(exception.getMessage(), is("2 is not divisible by 3"));
    }

    @Test
    public void move_findByName_fails_exception_test() {

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            testee.move(Spielzug.builder().move(2).value(1).build(), "spieler");
        });

        assertThat(exception.getMessage(), is("Player not found: spieler"));
    }

    @Test
    public void move_not_paired_with_opponent_exception() {

        Spieler spieler = new Spieler();
        spieler.setName("spieler");

        when(spielerRepository.findByName("spieler")).thenReturn(Optional.of(spieler));

        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> {
            testee.move(Spielzug.builder().move(1).value(8).build(), "spieler");
        });

        assertThat(exception.getMessage(), is("You have not been paired with an opponent"));

    }

    @Test
    public void move_call_delegate_once() {

        LogCaptor logCaptor = LogCaptor.forClass(SpielDelegate.class);
        logCaptor.setLogLevelToDebug();

        Spieler spieler = new Spieler();
        spieler.setName("spieler");

        Spieler gegenspieler = new Spieler();
        gegenspieler.setName("gegenspieler");

        spieler.setGegenspieler(gegenspieler);

        when(spielerRepository.findByName("spieler")).thenReturn(Optional.of(spieler));

        testee.move(Spielzug.builder().move(1).value(8).build(), "spieler");

        verify(meldungService, times(1)).notifyPlayer(eq("gegenspieler"), any());
        verifyNoMoreInteractions(meldungService);

        assertThat(logCaptor.getDebugLogs().get(0), is("Spieler spieler, value 8, move 1, sum 9. Final value 3"));
    }

    @Test
    public void move_call_delegate_twice() {

        LogCaptor logCaptor = LogCaptor.forClass(SpielDelegate.class);
        logCaptor.setLogLevelToDebug();

        Spieler spieler = new Spieler();
        spieler.setName("spieler");

        Spieler gegenspieler = new Spieler();
        gegenspieler.setName("gegenspieler");

        spieler.setGegenspieler(gegenspieler);

        when(spielerRepository.findByName("spieler")).thenReturn(Optional.of(spieler));

        testee.move(Spielzug.builder().move(1).value(2).build(), "spieler");

        verify(meldungService, times(1)).notifyPlayer(eq("spieler"), any());
        verify(meldungService, times(1)).notifyPlayer(eq("gegenspieler"), any());
        verifyNoMoreInteractions(meldungService);

        assertThat(logCaptor.getDebugLogs().get(0), is("Spieler spieler, value 2, move 1, sum 3. Final value 1"));
    }
}