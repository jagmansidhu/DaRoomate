package com.roomate.app.dto;

import com.roomate.app.entities.UtilityEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class UtilityDto {
    private UUID id;
    private String utilityName;
    private Double utilityPrice;
    private UUID roomId;

    public UtilityDto(UtilityEntity entity) {
        this.id = entity.getId();
        this.utilityName = entity.getUtilityName();
        this.utilityPrice = entity.getUtilityPrice();
        this.roomId = entity.getRoom() != null ? entity.getRoom().getId() : null;
    }

    // getters and setters
}
