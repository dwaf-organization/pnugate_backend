package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice_file")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_file_code")
    private Integer noticeFileCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_code", nullable = false)
    private Notice notice;
    
    @Column(name = "notice_file_name", nullable = false, length = 100)
    private String noticeFileName;
    
    @Column(name = "notice_file_link", nullable = false, length = 500)
    private String noticeFileLink;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}