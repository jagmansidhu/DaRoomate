package com.roomate.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @NotNull
    private Long id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Column(unique = true)
    private String email;
    private String phone;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "addressId", referencedColumnName = "id")
    private AddressEntity address;

    protected UserEntity() {}

    public UserEntity(String firstName, String lastName, String mail, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = mail;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s', email='%s', phone='%s']",
                id, firstName, lastName, email, phone);
    }
}
