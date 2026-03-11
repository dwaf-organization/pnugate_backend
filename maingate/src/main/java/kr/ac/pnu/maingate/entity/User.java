package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_mst")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private Integer userCode;
    
    @Column(name = "user_name", nullable = false, length = 10)
    private String userName;
    
    @Column(name = "user_id", nullable = false, length = 50, unique = true)
    private String userId;
    
    @Column(name = "user_pw", nullable = false, length = 100)
    private String userPw;
    
    @Column(length = 50)
    private String email;
    
    @Column(length = 50)
    private String phone;
    
    @Column(length = 50)
    private String title;
    
    @Column(length = 100)
    private String profile;
    
    @Column(length = 50)
    private String provider;
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}