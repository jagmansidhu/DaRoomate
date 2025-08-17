package com.roomate.app.service.implementation;

import com.roomate.app.dto.ChoreCreateDto;
import com.roomate.app.entities.ChoreEntity;
import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import com.roomate.app.repository.ChoreRepository;
import com.roomate.app.repository.RoomMemberRepository;
import com.roomate.app.repository.RoomRepository;
import com.roomate.app.service.ChoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChoreServiceImplt implements ChoreService {
    private final RoomRepository roomRepository;
    private final ChoreRepository choreRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Override
    public List<ChoreEntity> distributeChores(UUID roomId, ChoreCreateDto choreDTO) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));

        List<RoomMemberEntity> roomMembers = roomMemberRepository.findByRoomID(roomId);
        if (roomMembers.isEmpty()) {
            throw new IllegalStateException("No members in room to assign chores to");
        }

        List<ChoreEntity> createdChores = new ArrayList<>();
        int totalInstances = calculateTotalInstances(choreDTO);
        int memberIndex = 0;

        for (int i = 0; i < totalInstances; i++) {
            ChoreEntity chore = new ChoreEntity();
            chore.setChoreName(choreDTO.getChoreName());
            chore.setFrequency(choreDTO.getFrequency());
            chore.setChoreFrequencyUnitEnum(choreDTO.getFrequencyUnit());
            chore.setRoom(room);
            chore.setAssignedToMember(roomMembers.get(memberIndex % roomMembers.size()));
            chore.setDueAt(calculateDueDate(i, choreDTO));

            choreRepository.save(chore);
            createdChores.add(chore);
            memberIndex++;
        }

        return createdChores;
    }

    @Override
    public int calculateTotalInstances(ChoreCreateDto choreDTO) {
        return switch (choreDTO.getFrequencyUnit()) {
            case DAILY -> choreDTO.getFrequency() * 30;    // Month
            case WEEKLY -> choreDTO.getFrequency() * 4;    // Month
            case BIWEEKLY -> choreDTO.getFrequency() * 2;  // Month
            case MONTHLY -> choreDTO.getFrequency();
        };
    }

    @Override
    public LocalDateTime calculateDueDate(int instanceNumber, ChoreCreateDto choreDTO) {
        LocalDateTime now = LocalDateTime.now();
        return switch (choreDTO.getFrequencyUnit()) {
            case DAILY -> now.plusDays(instanceNumber);
            case WEEKLY -> now.plusWeeks(instanceNumber / choreDTO.getFrequency());
            case BIWEEKLY -> now.plusWeeks((instanceNumber / choreDTO.getFrequency()) * 2);
            case MONTHLY -> now.plusMonths(instanceNumber);
        };
    }

    @Override
    public void redistributeChores(UUID roomId) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));

        List<RoomMemberEntity> roomMembers = roomMemberRepository.findByRoomId(roomId);
        List<ChoreEntity> chores = choreRepository.findByRoomAndDueAtAfter(room, LocalDateTime.now());

        int memberIndex = 0;
        for (ChoreEntity chore : chores) {
            chore.setAssignedToMember(roomMembers.get(memberIndex % roomMembers.size()));
            choreRepository.save(chore);
            memberIndex++;
        }
    }

    @Override
    public List<ChoreEntity> getChoresByRoomId(UUID roomId) {
        return List.of();
    }

    @Override
    public void deleteChore(UUID choreId) {

    }
}