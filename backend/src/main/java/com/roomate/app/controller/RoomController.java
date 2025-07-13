package com.roomate.app.controller;

import com.auth0.jwt.JWT;
import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/rooms")
public class RoomController {

    @GetMapping
    public ResponseEntity<List<RoomDto>> getUserRooms(@AuthenticationPrincipal Jwt jwt){
        return (ResponseEntity<List<RoomDto>>) List.of("");
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomRequest request, @AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(new RoomDto());
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<RoomDto> joinRoom(@PathVariable String roomCode, @AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(new RoomDto());
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable UUID roomId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new RoomDto());
    }

    @PutMapping("/{roomId}/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable UUID roomId, @PathVariable UUID memberId, @RequestBody UpdateMemberRoleRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().build();
    }
}