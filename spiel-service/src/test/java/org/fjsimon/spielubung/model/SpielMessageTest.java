package org.fjsimon.spielubung.model;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class SpielMessageTest {

    @Test
    public void builderTest() {

        SpielMessage spielMessage = SpielMessage.builder()
                .spielStatus(SpielStatus.WARTEN)
                .primary(true)
                .description("description")
                .winner(true)
                .value(10)
                .build();


        assertThat(spielMessage.getSpielStatus().toString(), is("WARTEN"));
        assertThat(spielMessage.getDescription(), is("description"));
        assertThat(spielMessage.isPrimary(), is(true));
        assertThat(spielMessage.isWinner(), is(true));
        assertThat(spielMessage.getValue(), is(10));
    }
}