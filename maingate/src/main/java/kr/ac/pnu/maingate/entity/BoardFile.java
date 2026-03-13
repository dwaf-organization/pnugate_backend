package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_file")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_file_code")
    private Integer boardFileCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_code", nullable = false)
    private Board board;
    
    @Column(name = "board_file_name", nullable = false, length = 100)
    private String boardFileName;
    
    @Column(name = "board_file_link", nullable = false, length = 500)
    private String boardFileLink;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}