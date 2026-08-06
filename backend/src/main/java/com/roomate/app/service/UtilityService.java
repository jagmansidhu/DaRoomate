package com.roomate.app.service;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.dto.UtilityDto;
import com.roomate.app.entities.UtilityEntity;

import java.util.List;
import java.util.UUID;

public interface UtilityService {
    List<UtilityEntity> createUtility(UtilityCreateDto dto, String email);

    List<UtilityDto> getUtilitiesByRoom(UUID roomId, String email);

    List<UtilityDto> getUtilitiesByRoomandMemberId(UUID roomId, UUID memberId, String email);

    List<UtilityDto> getUpcomingUtilities(String id);

    void deleteUtility(UUID utilityId, String email);

    UtilityDto updateCompletion(UUID utilityId, String userEmail, boolean completed);
}
