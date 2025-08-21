package com.roomate.app.entities;

import com.roomate.app.entities.room.RoomEntity;
import com.roomate.app.entities.room.RoomMemberEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class UtilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String utilityName;
    private String description;

    @Enumerated(EnumType.STRING)
    private ChoreFrequencyUnitEnum choreFrequencyUnitEnum;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastCompletedAt;

    private LocalDateTime dueAt;

    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_member_id")
    private RoomMemberEntity assignedToMember;


}
