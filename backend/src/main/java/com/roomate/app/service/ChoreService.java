package com.roomate.app.service;

import com.roomate.app.dto.ChoreCreateDto;
import com.roomate.app.entities.ChoreEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChoreService {
    List<ChoreEntity> distributeChores(UUID roomId, ChoreCreateDto choreDTO);
    void redistributeChores(UUID roomId);
    List<ChoreEntity> getChoresByRoomId(UUID roomId);
    void deleteChore(UUID choreId);
    void deleteChoresByType(UUID roomId, String choreName);
    int calculateTotalInstances(ChoreCreateDto choreDTO);
    LocalDateTime calculateDueDate(int instanceNumber, ChoreCreateDto choreDTO);
}
