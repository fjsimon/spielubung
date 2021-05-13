package org.fjsimon.spielubung.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class SpielerTest {

    @Test
    public void toStringTest() {

        Spieler spieler = new Spieler();
        spieler.setId(1L);

        String result = spieler.toString();
        assertThat(result, is("Spieler(id=1, name=null, available=false, primary=false)"));
    }
}