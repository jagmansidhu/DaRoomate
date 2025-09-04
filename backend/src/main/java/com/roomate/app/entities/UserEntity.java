package com.roomate.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roomate.app.entities.roleEntity.RolesEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @NotNull
    @Column(unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    private String phone;

    @Column(nullable = false)
    private boolean enabled = false;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "addressId", referencedColumnName = "id")
//    @JsonIgnore
//    private AddressEntity address;
    // TODO Remove EAGER loading and add dto to user/login
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<RolesEntity> roles = new HashSet<>();
//    @OneToMany(mappedBy = "requester", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//            orphanRemoval = true, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private Set<FriendEntity> sentFriendRequests = new HashSet<>();
//    @OneToMany(mappedBy = "addressee", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//            orphanRemoval = true, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private Set<FriendEntity> receivedFriendRequests = new HashSet<>();

    public UserEntity() {}

    public UserEntity(String authId, String firstName, String lastName, String email, String phone) {
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
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "UserEntity[id=%d, firstName='%s', lastName='%s', email='%s', phone='%s']",
                id, firstName, lastName, email, phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phone);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}