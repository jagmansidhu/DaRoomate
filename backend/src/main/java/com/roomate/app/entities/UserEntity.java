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
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    private String phone;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    @JsonIgnore
    private AddressEntity address;
    @ManyToMany(targetEntity = RolesEntity.class)
    @JsonIgnore
    private Set<RolesEntity> roles;

    protected UserEntity() {}

    public UserEntity(String firstName, String lastName, String mail, String password, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = mail;
        this.password = password;
        this.phone = phone;

    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s', email='%s', phone='%s']",
                id, firstName, lastName, email, phone);
    }
}
