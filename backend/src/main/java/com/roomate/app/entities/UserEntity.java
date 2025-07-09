package com.roomate.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roomate.app.entities.friendEntity.FriendEntity;
import com.roomate.app.entities.roleEntity.RolesEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String authId;
    private String firstName;
    private String lastName;
    @NotNull
    @Column(unique = true)
    private String email;
    private String phone;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    @JsonIgnore
    private AddressEntity address;
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<RolesEntity> roles = new HashSet<>();
    @OneToMany(mappedBy = "requester", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<FriendEntity> sentFriendRequests = new HashSet<>();
    @OneToMany(mappedBy = "addressee", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<FriendEntity> receivedFriendRequests = new HashSet<>();

    public UserEntity() {}

    public UserEntity(String authId, String firstName, String lastName, String email, String phone) {
        this.authId = authId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    public UserEntity(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public UserEntity(String authId, String email, String firstName, String lastName) {
        this.authId = authId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserEntity(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phoneNumber;
    }

    public UserEntity(String authId, String email) {
        this.authId = authId;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "UserEntity[id=%d, authId='%s', firstName='%s', lastName='%s', email='%s', phone='%s']",
                id, authId, firstName, lastName, email, phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(authId, that.authId) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authId, firstName, lastName, email, phone);
    }
}