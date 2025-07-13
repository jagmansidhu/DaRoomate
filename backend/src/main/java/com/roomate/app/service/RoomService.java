package com.roomate.app.service;

import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    List<RoomDto> getUserRooms(String userId);
    RoomDto createRoom(CreateRoomRequest request, String headRoommateId, String headRoommateName, String headRoommateEmail);
    RoomDto joinRoom(String roomCode, String userId, String userName, String userEmail);
    RoomDto getRoomById(UUID roomId, String userId);
    void updateMemberRole(UUID roomId, UUID memberId, UpdateMemberRoleRequest request, String requestingUserId);
    boolean isHeadRoommate(UUID roomId, String userId);
    boolean isRoomMember(UUID roomId, String userId);
}
