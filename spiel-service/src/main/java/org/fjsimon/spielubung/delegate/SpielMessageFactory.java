package org.fjsimon.spielubung.delegate;

import org.fjsimon.spielubung.model.SpielMessage;
import org.fjsimon.spielubung.model.SpielStatus;
import org.fjsimon.spielubung.model.Spieler;

public class SpielMessageFactory {

    public static SpielMessage aWartenMessage() {
        return SpielMessage.builder()
            .spielStatus(SpielStatus.WARTEN)
            .primary(true)
            .description("Waiting ...")
            .build();
    }

    public static SpielMessage aSpielenMessage(int value) {
        return SpielMessage.builder()
            .spielStatus(SpielStatus.SPIELEN)
            .value(value)
            .build();
    }

    public static SpielMessage aSpielEndeMessage(boolean winner) {
        return SpielMessage.builder()
            .spielStatus(SpielStatus.SPIEL_ENDE)
            .winner(winner)
            .build();
    }

    public static SpielMessage aStartenMessage(Spieler spieler) {
        return SpielMessage
            .builder()
            .spielStatus(SpielStatus.STARTEN)
            .gegenspieler(spieler.getGegenspieler().getName())
            .primary(spieler.isPrimary())
            .description(String.format("%s requested", spieler.getGegenspieler().getName()))
            .build();
    }

    public static SpielMessage aTrennenMessage(String disconnectedPlayerName) {
        return SpielMessage.builder()
            .spielStatus(SpielStatus.TRENNEN)
            .description(String.format("%s disconnected", disconnectedPlayerName))
            .build();
    }

}
