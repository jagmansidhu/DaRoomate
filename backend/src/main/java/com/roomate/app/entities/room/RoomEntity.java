package com.roomate.app.entities.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "room")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @NotNull
    private String address;

//    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(unique = true)
    private String roomCode;

    @NotNull
    @Column(name = "head_roommate_id")
    private String headRoommateId; //AuthId

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomMemberEntity> members = new ArrayList<>();

    public RoomEntity() {
        this.createdAt = LocalDateTime.now();
    }
}