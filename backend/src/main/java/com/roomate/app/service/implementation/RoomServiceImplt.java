package com.roomate.app.service.implementation;

import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;
import com.roomate.app.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomServiceImplt implements RoomService {
    @Override
    public List<RoomDto> getUserRooms(String userId) {
        return List.of();
    }

    @Override
    public RoomDto createRoom(CreateRoomRequest request, String headRoommateId, String headRoommateName, String headRoommateEmail) {
        return null;
    }

    @Override
    public RoomDto joinRoom(String roomCode, String userId, String userName, String userEmail) {
        return null;
    }

    @Override
    public RoomDto getRoomById(UUID roomId, String userId) {
        return null;
    }

    @Override
    public void updateMemberRole(UUID roomId, UUID memberId, UpdateMemberRoleRequest request, String requestingUserId) {

    }

    @Override
    public boolean isHeadRoommate(UUID roomId, String userId) {
        return false;
    }

    @Override
    public boolean isRoomMember(UUID roomId, String userId) {
        return false;
    }
}
