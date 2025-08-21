package com.roomate.app.controller;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.entities.UtilityEntity;
import com.roomate.app.service.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/utility")
public class UtilityControler {
    private final UtilityService utilityService;

    @PostMapping("/create")
    public ResponseEntity<UtilityEntity> createUtility(@RequestBody UtilityCreateDto dto) {
        UtilityEntity utility = utilityService.createUtility(dto);
        return ResponseEntity.ok(utility);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<UtilityEntity>> getUtilitiesByRoom(@PathVariable UUID roomId) {
        return ResponseEntity.ok(utilityService.getUtilitiesByRoom(roomId));
    }


}
