package com.roomate.app.controller;

import com.roomate.app.dto.ChoreCreateDto;
import com.roomate.app.entities.ChoreEntity;
import com.roomate.app.service.ChoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chores")
public class ChoreController {
    private final ChoreService choreService;

    @PostMapping("/room/createChores/{roomId}")
    public ResponseEntity<List<ChoreEntity>> createChores(@PathVariable UUID roomId, @RequestBody ChoreCreateDto choreDTO) {
        List<ChoreEntity> chores = choreService.distributeChores(roomId, choreDTO);
        return ResponseEntity.ok(chores);
    }

    @PostMapping("/room/{roomId}/redistribute")
    public ResponseEntity<Void> redistributeChores(@PathVariable UUID roomId) {
        choreService.redistributeChores(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChoreEntity>> getRoomChores(@PathVariable UUID roomId) {
        List<ChoreEntity> chores = choreService.getChoresByRoomId(roomId);
        return ResponseEntity.ok(chores);
    }

    @DeleteMapping("/{choreId}")
    public ResponseEntity<Void> deleteChore(@PathVariable UUID choreId) {
        choreService.deleteChore(choreId);
        return ResponseEntity.noContent().build();
    }
}