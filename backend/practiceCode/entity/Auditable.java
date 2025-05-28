//package com.roomate.app.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.roomate.app.domain.RequestContext;
//import com.roomate.app.exception.ApiException;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import org.springframework.util.AlternativeJdkIdGenerator;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
//@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
//public abstract class Auditable {
//    @Id
//    @SequenceGenerator(name = "private_key_seq", sequenceName = "private_key_seq", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "private_key_seq")
//    @Column(name = "id", updatable = false)
//    private Long id;
//    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();
//    @NotNull
//    private Long createdBy;
//    @NotNull
//    private Long updatedBy;
//    @NotNull
//    @CreatedDate
//    @Column(name = "created_at",nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//    @CreatedDate
//    @Column(name = "updated_at",nullable = false)
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void beforePersist() {
//        var userId = 0L;//RequestContext.getUserId();
////        if (userId == null) {
////            throw new ApiException("User id is not set, so cannot persist entity");
////        }
//        setCreatedAt(LocalDateTime.now());
//        setCreatedBy(userId);
//        setUpdatedBy(userId);
//        setUpdatedAt(LocalDateTime.now());
//    }
//
//    @PreUpdate
//    protected void beforeUpdate() {
//        var userId = 0L; //RequestContext.getUserId();
////        if (userId == null) {
////            throw new ApiException("User id is not set, so cannot update entity");
////        }
//        setCreatedAt(LocalDateTime.now());
//        setCreatedBy(userId);
//    }
//}
