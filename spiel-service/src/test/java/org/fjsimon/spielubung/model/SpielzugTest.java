package org.fjsimon.spielubung.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class SpielzugTest {

    @Test
    public void builderTest() {

        Spielzug spielzug = Spielzug.builder()
                .move(1)
                .value(1)
                .build();

        assertThat(spielzug.getMove(), is(1));
        assertThat(spielzug.getValue(), is(1));
    }
}