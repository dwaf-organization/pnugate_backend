package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resources")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Resources {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resources_code")
    private Integer resourcesCode;
    
    @Column(name = "resources_title", nullable = false, length = 100)
    private String resourcesTitle;
    
    @Column(name = "resources_writer", nullable = false, length = 50)
    private String resourcesWriter;
    
    @Column(name = "resources_contents", nullable = false, columnDefinition = "TEXT")
    private String resourcesContents;
    
    @Column(name = "resources_views", nullable = false)
    @Builder.Default
    private Integer resourcesViews = 0;
    
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
    
    // 파일 관계
    @OneToMany(mappedBy = "resources", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResourcesFile> files = new ArrayList<>();
}