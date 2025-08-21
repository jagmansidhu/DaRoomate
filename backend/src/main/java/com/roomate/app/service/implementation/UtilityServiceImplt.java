package com.roomate.app.service.implementation;

import com.roomate.app.dto.UtilityCreateDto;
import com.roomate.app.entities.UtilityEntity;
import com.roomate.app.entities.UtilDistributionEnum;
import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import com.roomate.app.repository.RoomMemberRepository;
import com.roomate.app.repository.RoomRepository;
import com.roomate.app.repository.UtilityRepository;
import com.roomate.app.service.UtilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UtilityServiceImplt implements UtilityService {

    private final UtilityRepository utilityRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Override
    public UtilityEntity createUtility(UtilityCreateDto dto) {
        com.roomate.app.entities.room.RoomEntity room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        UtilityEntity utility = new UtilityEntity();
        utility.setUtilityName(dto.getUtilityName());
        utility.setDescription(dto.getDescription());
        utility.setUtilityPrice(dto.getUtilityPrice());
        utility.setUtilDistributionEnum(dto.getUtilDistributionEnum());
        utility.setRoom(room);

        // Handle equal split logic
        if (dto.getUtilDistributionEnum() == UtilDistributionEnum.EQUALSPLIT) {
            List<RoomMemberEntity> members = roomMemberRepository.findByRoomId(dto.getRoomId());
            double share = dto.getUtilityPrice() / members.size();
            for (RoomMemberEntity member : members) {
                // You could either create a join table entity UtilityShareEntity
                // OR assign one at a time (if just tracking responsibility)
                // For now, let’s assign first member to utility for demo
                utility.setAssignedToMember(member);
                break;
            }
        }

        // Handle custom split logic
        if (dto.getUtilDistributionEnum() == UtilDistributionEnum.CUSTOMSPLIT) {
            dto.getCustomSplit().forEach((memberId, percentage) -> {
                RoomMemberEntity member = roomMemberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found"));
                double shareAmount = (percentage / 100.0) * dto.getUtilityPrice();

                // Same as above: you’ll probably want a UtilityShareEntity here
                // so each member gets a record of their assigned cost
            });
        }

        return utilityRepository.save(utility);
    }
}
