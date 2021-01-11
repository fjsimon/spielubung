package org.fjsimon.spielubung.model;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Spielzug {

    @Getter
    private int value;

    @Getter
    private int move;
}
