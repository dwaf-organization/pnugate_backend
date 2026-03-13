package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources_file")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResourcesFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resources_file_code")
    private Integer resourcesFileCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resources_code", nullable = false)
    private Resources resources;
    
    @Column(name = "resources_file_name", nullable = false, length = 100)
    private String resourcesFileName;
    
    @Column(name = "resources_file_link", nullable = false, length = 500)
    private String resourcesFileLink;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}