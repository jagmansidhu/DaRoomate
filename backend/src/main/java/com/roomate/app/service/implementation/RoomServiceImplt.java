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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomServiceImplt implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImplt.class);

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public RoomServiceImplt(UserRepository userRepository, RoomRepository roomRepository, RoomMemberRepository roomMemberRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Override
    @Transactional
    public List<RoomDto> getUserRooms(String authId) {
        UserEntity user = userRepository.getUserEntityByAuthId(authId);

        if (user == null) {
            return List.of();
        }

        List<RoomEntity> roomEntities = roomRepository.findByMemberUserId(user.getId());
        System.out.println(roomEntities.getFirst().getRoomCode());

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

        RoomEntity room = new RoomEntity(request.getName(), request.getAddress(), request.getDescription(), roomCode, headRoommateId, new ArrayList<>());

        RoomMemberEntity roomMemberEntity = new RoomMemberEntity(room, user, RoomMemberEnum.HEAD_ROOMMATE);

        room.getMembers().add(roomMemberEntity);

        RoomEntity savedRoom;
        try {
            savedRoom = roomRepository.save(room);
            logger.info("Saved room and its members: {}", savedRoom);
        } catch (Exception e) {
            logger.error("Error saving room or its members: {}", e.getMessage(), e);
            throw new UserApiError("Failed to create room: " + e.getMessage());
        }

        return convertToRoomDto(savedRoom);
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
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new UserApiError("Room not found with ID: " + roomId));

        UserEntity requestingUser = userRepository.getUserEntityByAuthId(authId);

        if (requestingUser == null || !isRoomMember(roomId, authId)) {
            throw new UserApiError("User is not authorized to view this room.");
        }

        return convertToRoomDto(room);
    }

    @Override
    public void removeMemberFromRoom(UUID roomId, UUID memberId, String removerAuthId) throws UserApiError {
        RoomMemberEntity member = roomMemberRepository.getRoomMemberEntityById(memberId)
                .orElseThrow(() -> new UserApiError("Room member not found with ID: " + memberId));
        RoomEntity room = roomRepository.getRoomEntityById(roomId)
                .orElseThrow(() -> new UserApiError("Room not found with ID: " + roomId));


        if (!room.getHeadRoommateId().equals(removerAuthId) && !member.getUser().getAuthId().equals(removerAuthId)) {
            throw new UserApiError("Not authorized to remove this member.");
        }

        if (member.getRole() == RoomMemberEnum.HEAD_ROOMMATE) {
            throw new UserApiError("Cannot remove the head roommate from the room.");
        }

        roomMemberRepository.deleteById(memberId);
    }

    @Override
    public void removeRoom(UUID roomId, String requesterAuthId) throws UserApiError {
        UserEntity user = userRepository.getUserEntityByAuthId(requesterAuthId);
        RoomEntity room = roomRepository.getRoomEntityById(roomId)
                .orElseThrow(() -> new UserApiError("Room not found with ID: " + roomId));
        if (user == null) {
            throw new UserApiError("User not found with ID: " + requesterAuthId);
        }

        if (!room.getHeadRoommateId().equals(requesterAuthId)) {
            throw new UserApiError("Not authorized to delete room.");
        }

        roomMemberRepository.deleteAllByRoomId(roomId);

        roomRepository.deleteById(roomId);
    }

    @Override
    @Transactional
    public void updateMemberRole(UUID roomId, UUID memberId, UpdateMemberRoleRequest request, String requestingUserId) throws UserApiError {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new UserApiError("Room not found with ID: " + roomId));

        UserEntity requestingUser = userRepository.getUserEntityByAuthId(requestingUserId);
        if (requestingUser == null || !room.getHeadRoommateId().equals(requestingUserId)) {
            throw new UserApiError("Only the head roommate can update member roles.");
        }

        RoomMemberEntity member = roomMemberRepository.findById(memberId)
                .orElseThrow(() -> new UserApiError("Member not found with ID: " + memberId));

        if (!member.getRoom().getId().equals(roomId)) {
            throw new UserApiError("Member does not belong to the specified room.");
        }

        if (member.getRole() == RoomMemberEnum.HEAD_ROOMMATE) {
            throw new UserApiError("Cannot change the head roommate's role directly. Transfer head roommate status first.");
        }

        member.setRole(request.getRole());
        member.setUpdatedAt(LocalDateTime.now());

        roomMemberRepository.save(member);
    }

    @Override
    public boolean isRoomMember(UUID roomId, String authId) {
        UserEntity user = userRepository.getUserEntityByAuthId(authId);
        if (user == null) {
            return false;
        }
        return roomMemberRepository.findByRoomIdAndUserId(roomId, user.getId()).isPresent();
    }

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

        List<RoomMemberDto> memberDtos = new ArrayList<>();
        if (room.getMembers() != null) {
            for (RoomMemberEntity member : room.getMembers()) {
                if (member == null || member.getUser() == null) continue;
                RoomMemberDto memberDto = new RoomMemberDto();
                memberDto.setId(member.getId());
                memberDto.setJoinedAt(member.getJoinedAt());
                memberDto.setUserId(member.getUser().getId());
                memberDto.setName(member.getUser().getFirstName());
                memberDto.setRole(member.getRole());
                memberDtos.add(memberDto);
            }
        }

        dto.setMembers(memberDtos.isEmpty() ? null : memberDtos);

        return dto;
    }

    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (roomRepository.existsByRoomCode(roomCode));
        return roomCode;
    }
}