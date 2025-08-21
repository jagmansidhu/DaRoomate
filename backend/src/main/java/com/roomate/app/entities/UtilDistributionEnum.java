package com.roomate.app.entities;

import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UtilDistributionEnum {
    EQUALSPLIT,
    CUSTOMSPLIT
}
