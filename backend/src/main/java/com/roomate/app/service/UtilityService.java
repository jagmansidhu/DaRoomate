package com.roomate.app.service;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.entities.UtilityEntity;

public interface UtilityService {
    UtilityEntity createUtility(UtilityCreateDto dto);
}
