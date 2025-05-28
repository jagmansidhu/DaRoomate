//package com.roomate.app.entity;
//
//import com.fasterxml.jackson.annotation.*;
//import jakarta.persistence.Entity;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//import lombok.*;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import java.util.UUID;
//
//@Getter
//@Setter
//@ToString
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "confirmations")
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
//public class ConfirmationEntity extends Auditable{
//    private String code;
//    @OneToOne(targetEntity = UserEntity.class, fetch = jakarta.persistence.FetchType.EAGER)
//    @JoinColumn(name = "user_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    @JsonProperty("user_id")
//    private UserEntity userEntity;
//
//    public ConfirmationEntity(UserEntity userEntity) {
//        this.userEntity = userEntity;
//        this.code = UUID.randomUUID().toString();
//    }
//}
