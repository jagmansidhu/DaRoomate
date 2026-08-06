package com.roomate.app.service;

import com.roomate.app.entities.room.RoomMemberEntity;
import com.roomate.app.entities.room.RoomMemberEnum;
import com.roomate.app.exceptions.ForbiddenApiError;
import com.roomate.app.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomAuthorizationService {
    private final RoomMemberRepository roomMemberRepository;

    public RoomMemberEntity assertRoomMember(UUID roomId, String email) {
        return roomMemberRepository.findByRoomIdAndUserEmail(roomId, email)
                .orElseThrow(() -> new ForbiddenApiError("You are not a member of this room"));
    }

    public RoomMemberEntity assertRoomRole(UUID roomId, String email, RoomMemberEnum... allowedRoles) {
        RoomMemberEntity member = assertRoomMember(roomId, email);
        Set<RoomMemberEnum> allowed = EnumSet.copyOf(Arrays.asList(allowedRoles));
        if (!allowed.contains(member.getRole())) {
            throw new ForbiddenApiError("You do not have permission to perform this action");
        }
        return member;
    }
}
