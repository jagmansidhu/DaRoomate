package com.roomate.app.service;

import com.roomate.app.dto.ChoreCreateDto;
import com.roomate.app.dto.ChoreDto;

import java.util.List;
import java.util.UUID;

public interface ChoreService {
    List<ChoreDto> distributeChores(UUID roomId, ChoreCreateDto choreDTO, String email);

    void redistributeChores(UUID roomId, String email);

    List<ChoreDto> getChoresByRoomId(UUID roomId, String email);

    void deleteChore(UUID choreId, String email);

    void deleteChoresByType(UUID roomId, String choreName, String email);

    List<ChoreDto> getChoresByUserId(String id);

    ChoreDto updateCompletion(UUID choreId, String userEmail, boolean completed);
}
