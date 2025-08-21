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
import jakarta.transaction.Transactional;
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
    @Transactional
    public List<ChoreEntity> distributeChores(UUID roomId, ChoreCreateDto choreDTO) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));

        List<RoomMemberEntity> roomMembers = roomMemberRepository.findByRoomID(roomId);
        if (roomMembers.isEmpty()) {
            throw new IllegalStateException("No members in room to assign chores to");
        }

        if (choreDTO.getDeadline() == null) {
            throw new IllegalArgumentException("Deadline is required");
        }
        LocalDateTime now = LocalDateTime.now();
        if (choreDTO.getDeadline().isAfter(now.plusYears(1))) {
            throw new IllegalArgumentException("Deadline cannot be more than one year from now");
        }
        if (choreDTO.getDeadline().isBefore(now)) {
            throw new IllegalArgumentException("Deadline must be in the future");
        }

        List<ChoreEntity> createdChores = new ArrayList<>();
        int memberIndex = 0;
        LocalDateTime dueDate = LocalDateTime.now();
        while (dueDate.isBefore(choreDTO.getDeadline()) || dueDate.isEqual(choreDTO.getDeadline())) {
            ChoreEntity chore = new ChoreEntity();
            chore.setChoreName(choreDTO.getChoreName());
            chore.setFrequency(choreDTO.getFrequency());
            chore.setChoreFrequencyUnitEnum(choreDTO.getFrequencyUnit());
            chore.setRoom(room);
            chore.setAssignedToMember(roomMembers.get(memberIndex % roomMembers.size()));
            chore.setDueAt(dueDate.plusMonths(8));

            choreRepository.save(chore);
            createdChores.add(chore);
            memberIndex++;
            // Increment dueDate based on frequency unit
            switch (choreDTO.getFrequencyUnit()) {
                case WEEKLY -> dueDate = dueDate.plusWeeks(1);
                case BIWEEKLY -> dueDate = dueDate.plusWeeks(2);
                case MONTHLY -> dueDate = dueDate.plusMonths(1);
            }
        }

        return createdChores;
    }

    @Override
    public int calculateTotalInstances(ChoreCreateDto choreDTO) {
        return switch (choreDTO.getFrequencyUnit()) {
            case WEEKLY -> choreDTO.getFrequency() * 4;
            case BIWEEKLY -> choreDTO.getFrequency() * 2;
            case MONTHLY -> choreDTO.getFrequency();
        };
    }

    @Override
    public LocalDateTime calculateDueDate(int instanceNumber, ChoreCreateDto choreDTO) {
        LocalDateTime now = LocalDateTime.now();
        return switch (choreDTO.getFrequencyUnit()) {
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
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));
        return choreRepository.findByRoom(room);
    }

    @Override
    @Transactional
    public void deleteChore(UUID choreId) {
        choreRepository.deleteById(choreId);
    }

    @Override
    @Transactional
    public void deleteChoresByType(UUID roomId, String choreName) {
        RoomEntity room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));
        choreRepository.deleteAllByRoomIdAndChoreName(roomId, choreName);
    }
}