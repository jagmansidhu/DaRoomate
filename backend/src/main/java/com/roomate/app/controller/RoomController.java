package com.roomate.app.controller;

import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.InviteUserRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getUserRooms(@AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            List<RoomDto> rooms = roomService.getUserRooms(authId);
            if (rooms.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            String userName = jwt.getClaimAsString("name");
            String userEmail = jwt.getClaimAsString("email");
            
            RoomDto createdRoom = roomService.createRoom(request, authId, userName, userEmail);
            return ResponseEntity.ok(createdRoom);
        } catch (UserApiError e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserToRoom(@RequestBody InviteUserRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            roomService.inviteUserToRoom(request, authId);
            return ResponseEntity.ok().build();
        } catch (UserApiError e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<RoomDto> joinRoom(@PathVariable String roomCode, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            String userName = jwt.getClaimAsString("name");
            String userEmail = jwt.getClaimAsString("email");
            
            RoomDto joinedRoom = roomService.joinRoom(roomCode, authId, userName, userEmail);
            return ResponseEntity.ok(joinedRoom);
        } catch (UserApiError e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable UUID roomId, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            RoomDto room = roomService.getRoomById(roomId, authId);
            return ResponseEntity.ok(room);
        } catch (UserApiError e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{roomId}/members/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable UUID roomId, @PathVariable UUID memberId, 
                                                @RequestBody UpdateMemberRoleRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            roomService.updateMemberRole(roomId, memberId, request, authId);
            return ResponseEntity.ok().build();
        } catch (UserApiError e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable UUID roomId, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            roomService.leaveRoom(roomId, authId);
            return ResponseEntity.ok().build();
        } catch (UserApiError e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{roomId}/members/{memberId}")
    public ResponseEntity<Void> removeMemberFromRoom(@PathVariable UUID roomId, @PathVariable UUID memberId, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            roomService.removeMemberFromRoom(roomId, memberId, authId);
            return ResponseEntity.ok().build();
        } catch (UserApiError e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{roomId}/removeroom")
    public ResponseEntity<Void> removeRoom(@PathVariable UUID roomId, @AuthenticationPrincipal Jwt jwt) {
        try {
            String authId = jwt.getSubject();
            roomService.removeRoom(roomId, authId);
            return ResponseEntity.ok().build();
        } catch (UserApiError e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}