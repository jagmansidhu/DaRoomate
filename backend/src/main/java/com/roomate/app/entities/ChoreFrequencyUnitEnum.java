package com.roomate.app.entities;

import lombok.Getter;

@Getter
public enum ChoreFrequencyUnitEnum {
    DAILY(1),
    WEEKLY(7),
    BIWEEKLY(14),
    MONTHLY(30);

    private final int days;

    ChoreFrequencyUnitEnum(int days) {
        this.days = days;
    }

}
