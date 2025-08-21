package com.roomate.app.service;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.dto.UtilityDto;
import com.roomate.app.entities.UtilityEntity;

import java.util.List;
import java.util.UUID;

public interface UtilityService {
    UtilityEntity createUtility(UtilityCreateDto dto);
    List<UtilityDto> getUtilitiesByRoom(UUID roomId);

}
