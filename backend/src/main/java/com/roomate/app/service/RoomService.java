package com.roomate.app.service;

import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.InviteUserRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;
import com.roomate.app.exceptions.UserApiError;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    List<RoomDto> getUserRooms(String authId);
    RoomDto createRoom(CreateRoomRequest request, String headRoommateId, String headRoommateName, String headRoommateEmail) throws UserApiError;
    RoomDto joinRoom(String roomCode, String authId, String userName, String userEmail) throws UserApiError;
    RoomDto getRoomById(UUID roomId, String authId) throws UserApiError;
    void removeMemberFromRoom(UUID roomId, UUID memberId, String removerAuthId) throws UserApiError;
    void removeRoom(UUID roomId, String authId) throws UserApiError;
    void updateMemberRole(UUID roomId, UUID memberId, UpdateMemberRoleRequest request, String requestingUserId) throws UserApiError;
    void leaveRoom(UUID roomId, String authId) throws UserApiError;
    boolean isRoomMember(UUID roomId, String authId);
    void inviteUserToRoom(InviteUserRequest request, String authId) throws  UserApiError;
}
