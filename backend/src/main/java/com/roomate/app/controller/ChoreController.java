package com.roomate.app.controller;

import com.roomate.app.dto.ChoreCreateDto;
import com.roomate.app.dto.ChoreDto;
import com.roomate.app.entities.ChoreEntity;
import com.roomate.app.service.ChoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chores")
public class ChoreController {
    private final ChoreService choreService;

    @PostMapping("/room/createChores/{roomId}")
    public ResponseEntity<List<ChoreDto>> createChores(@PathVariable UUID roomId, @RequestBody List<ChoreCreateDto> choreDTOs) {
        List<ChoreEntity> allChores = choreDTOs.stream()
            .flatMap(dto -> choreService.distributeChores(roomId, dto).stream())
            .collect(Collectors.toList());
        List<ChoreDto> choreDtos = allChores.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(choreDtos);
    }

    @PostMapping("/room/{roomId}/redistribute")
    public ResponseEntity<Void> redistributeChores(@PathVariable UUID roomId) {
        choreService.redistributeChores(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChoreDto>> getRoomChores(@PathVariable UUID roomId) {
        List<ChoreEntity> chores = choreService.getChoresByRoomId(roomId);
        List<ChoreDto> choreDtos = chores.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(choreDtos);
    }

    @DeleteMapping("/{choreId}")
    public ResponseEntity<Void> deleteChore(@PathVariable UUID choreId) {
        choreService.deleteChore(choreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/room/{roomId}/type/{choreName}")
    public ResponseEntity<Void> deleteChoresByType(@PathVariable UUID roomId, @PathVariable String choreName) {
        choreService.deleteChoresByType(roomId, choreName);
        return ResponseEntity.noContent().build();
    }

    private ChoreDto toDto(ChoreEntity entity) {
        String assignedToMemberName = entity.getAssignedToMember() != null && entity.getAssignedToMember().getUser() != null
            ? entity.getAssignedToMember().getUser().getEmail()
            : null;
        return new ChoreDto(
            entity.getId(),
            entity.getChoreName(),
            entity.getFrequency(),
            entity.getChoreFrequencyUnitEnum().name(),
            entity.getDueAt(),
            entity.isCompleted(),
            assignedToMemberName
        );
    }
}