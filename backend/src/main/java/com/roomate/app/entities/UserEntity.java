package com.roomate.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
    @ManyToMany(targetEntity = RolesEntity.class)
    @JsonIgnore
    private Set<RolesEntity> roles;

    public UserEntity() {}

    public UserEntity(String authId, String firstName, String lastName, String email, String phone) {
        this.authId = authId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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
}