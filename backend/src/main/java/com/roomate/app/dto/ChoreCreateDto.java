package com.roomate.app.dto;

import com.roomate.app.entities.ChoreFrequencyUnitEnum;
import lombok.Data;

@Data
public class ChoreCreateDto {
    private String choreName;
    private int frequency;
    private ChoreFrequencyUnitEnum frequencyUnit;
}
