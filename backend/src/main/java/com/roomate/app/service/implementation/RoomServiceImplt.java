package com.roomate.app.service.implementation;

import com.roomate.app.dto.CreateRoomRequest;
import com.roomate.app.dto.RoomDto;
import com.roomate.app.dto.RoomMemberDto;
import com.roomate.app.dto.UpdateMemberRoleRequest;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import com.roomate.app.entities.room.RoomMemberEnum;
import com.roomate.app.exceptions.UserApiError;
import com.roomate.app.repository.RoomMemberRepository;
import com.roomate.app.repository.RoomRepository;
import com.roomate.app.repository.UserRepository;
import com.roomate.app.service.RoomService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomServiceImplt implements RoomService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public RoomServiceImplt(UserRepository userRepository, RoomRepository roomRepository, RoomMemberRepository roomMemberRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Override
    public List<RoomDto> getUserRooms(String authId) {
        UserEntity user = userRepository.getUserEntityByAuthId(authId);
        if (user == null) {
            return List.of();
        }

        List<RoomEntity> roomEntities = roomRepository.findByMemberUserId(user.getId());

        return roomEntities.stream().map(this::convertToRoomDto).toList();
    }

    @Override
    @Transactional
    public RoomDto createRoom(CreateRoomRequest request, String headRoommateId, String headRoommateName, String headRoommateEmail) throws UserApiError {
        UserEntity user = userRepository.getUserEntityByAuthId(headRoommateId);
        if (user == null) {
            throw new UserApiError("Head roommate user not found with ID: " + headRoommateId);
        }

        String roomCode = generateUniqueRoomCode();

        RoomEntity room = new RoomEntity(request.getName(), request.getAddress(), request.getDescription(), roomCode, headRoommateId, null);
        RoomMemberEntity roomMemberEntity = new RoomMemberEntity(room, user, RoomMemberEnum.HEAD_ROOMMATE);

        room.getMembers().add(roomMemberEntity);

        return convertToRoomDto(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomDto joinRoom(String roomCode, String authId, String userName, String userEmail) throws UserApiError {
        UserEntity user = userRepository.getUserEntityByAuthId(authId);
        if (user == null) {
            throw new UserApiError("User not found with ID: " + authId);
        }

        RoomEntity room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new UserApiError("Room not found with code: " + roomCode));

        boolean alreadyMember = room.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

        if (alreadyMember) {
            throw new UserApiError("User is already a member of this room.");
        }

        RoomMemberEntity member = new RoomMemberEntity(room, user, RoomMemberEnum.ROOMMATE);
        room.getMembers().add(member);

        return convertToRoomDto(roomRepository.save(room));
    }

    @Override
    public RoomDto getRoomById(UUID roomId, String authId) throws UserApiError {
        RoomEntity room = roomRepository.getRoomEntityById(roomId);
        if (room == null) {
            throw new UserApiError("Room not found with ID: " + roomId);
        }

        UserEntity requestingUser = userRepository.getUserEntityByAuthId(authId);
        if (requestingUser == null || !isRoomMember(roomId, authId)) {
            throw new UserApiError("User is not authorized to view this room.");
        }

        return convertToRoomDto(room);
    }

    @Override
    @Transactional
    public void updateMemberRole(UUID roomId, String targetUserId, UpdateMemberRoleRequest request, String requestingUserId) throws UserApiError {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new UserApiError("Room not found with ID: " + roomId));

        UserEntity requestingUser = userRepository.getUserEntityByAuthId(requestingUserId);
        if (requestingUser == null || !room.getHeadRoommateId().equals(requestingUserId)) {
            throw new UserApiError("Only the head roommate can update member roles.");
        }

        UserEntity targetUser = userRepository.getUserEntityByAuthId(targetUserId);
        if (targetUser == null) {
            throw new UserApiError("Target user not found with ID: " + targetUserId);
        }

        RoomMemberEntity member = roomMemberRepository.findByRoomIdAndUserId(roomId, targetUser.getId())
                .orElseThrow(() -> new UserApiError("Member not found in this room for user ID: " + targetUserId));

        if (member.getRole() == RoomMemberEnum.HEAD_ROOMMATE) {
            throw new UserApiError("Cannot change the head roommate's role directly. Transfer head roommate status first.");
        }

        member.setRole(request.getRole());
        member.setUpdatedAt(LocalDateTime.now());

        roomMemberRepository.save(member);
    }

    @Override
    public boolean isHeadRoommate(UUID roomId, String authId) {
        RoomEntity room = roomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return false;
        }
        return room.getHeadRoommateId().equals(authId);
    }

    @Override
    public boolean isRoomMember(UUID roomId, String authId) {
        UserEntity user = userRepository.getUserEntityByAuthId(authId);
        if (user == null) {
            return false;
        }
        return roomMemberRepository.findByRoomIdAndUserId(roomId, user.getId()).isPresent();
    }

    // Helper method to convert RoomEntity to RoomDto
    private RoomDto convertToRoomDto(RoomEntity room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setAddress(room.getAddress());
        dto.setDescription(room.getDescription());
        dto.setRoomCode(room.getRoomCode());
        dto.setHeadRoommateId(room.getHeadRoommateId());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());

        List<RoomMemberDto> memberDtos = room.getMembers().stream().map(member -> {
            RoomMemberDto memberDto = new RoomMemberDto();
            memberDto.setId(member.getId());
            memberDto.setJoinedAt(member.getJoinedAt());
            memberDto.setUserId(member.getUser().getId());
            memberDto.setName(member.getUser().getFirstName());
            memberDto.setRole(member.getRole());
            return memberDto;
        }).collect(Collectors.toList());

        dto.setMembers(memberDtos);

        return dto;
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Example: 8-character code
        } while (roomRepository.existsByRoomCode(roomCode));
        return roomCode;
    }
}