package com.roomate.app.controller;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.entities.UtilityEntity;
import com.roomate.app.service.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
