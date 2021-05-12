package org.fjsimon.spielubung.delegate;

import org.fjsimon.spielubung.model.SpielStatus;
import org.fjsimon.spielubung.model.Spieler;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class SpielMessageFactoryTest {

    @Test
    public void aWartenMessageTest() {

        assertThat(SpielMessageFactory.aWartenMessage().getSpielStatus(), is(SpielStatus.WARTEN));
        assertThat(SpielMessageFactory.aWartenMessage().isPrimary(), is(true));
        assertThat(SpielMessageFactory.aWartenMessage().getDescription(), is("Waiting ..."));
    }

    @Test
    public void aSpielenMessageTest() {

        assertThat(SpielMessageFactory.aSpielenMessage(1).getSpielStatus(), is(SpielStatus.SPIELEN));
        assertThat(SpielMessageFactory.aSpielenMessage(1).getValue(), is(1));
    }

    @Test
    public void aSpielEndeMessageTest() {

        assertThat(SpielMessageFactory.aSpielEndeMessage(true).getSpielStatus(), is(SpielStatus.SPIEL_ENDE));
        assertThat(SpielMessageFactory.aSpielEndeMessage(true).isWinner(), is(true));
        assertThat(SpielMessageFactory.aSpielEndeMessage(false).isWinner(), is(false));
    }

    @Test
    public void aStartenMessageTest() {

        Spieler spieler = new Spieler();
        spieler.setName("spieler");
        Spieler gegenspieler = new Spieler();
        gegenspieler.setName("gegenspieler");
        gegenspieler.setGegenspieler(spieler);
        spieler.setGegenspieler(gegenspieler);

        assertThat(SpielMessageFactory.aStartenMessage(spieler).getSpielStatus(), is(SpielStatus.STARTEN));
        assertThat(SpielMessageFactory.aStartenMessage(spieler).getGegenspieler(), is("gegenspieler"));
        assertThat(SpielMessageFactory.aStartenMessage(spieler).isPrimary(), is(false));
        assertThat(SpielMessageFactory.aStartenMessage(spieler).getDescription(), is("gegenspieler requested"));
    }

    @Test
    public void aTrennenMessageTest() {

        assertThat(SpielMessageFactory.aTrennenMessage("spieler").getSpielStatus(), is(SpielStatus.TRENNEN));
        assertThat(SpielMessageFactory.aTrennenMessage("spieler").getDescription(), is("spieler disconnected"));
    }
}