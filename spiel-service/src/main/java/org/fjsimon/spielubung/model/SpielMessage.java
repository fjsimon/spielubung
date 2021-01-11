package org.fjsimon.spielubung.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpielMessage {

    private SpielStatus spielStatus;

    private String description;

    private String gegenspieler;

    private boolean primaryPlayer;

    private int value;

    private int play;

    private boolean winner;
}

