package com.roomate.app.dto;

import com.roomate.app.entities.UtilDistributionEnum;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class UtilityCreateDto {
    private String utilityName;
    private String description;
    private double utilityPrice;
    private UtilDistributionEnum utilDistributionEnum;

    private UUID roomId;

    private Map<UUID, Double> customSplit;
}
