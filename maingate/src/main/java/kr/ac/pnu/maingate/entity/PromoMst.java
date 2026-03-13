package kr.ac.pnu.maingate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "promo_mst")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PromoMst {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_code")
    private Integer promoCode;
    
    @Column(name = "thumbnail_file", length = 500)
    private String thumbnailFile;
    
    @Column(name = "promo_link", nullable = false, length = 500)
    private String promoLink;
    
    @Column(name = "promo_title", nullable = false, length = 100)
    private String promoTitle;
    
    @Column(name = "promo_channel", length = 100)
    private String promoChannel;
    
    @Column(name = "promo_time", length = 50)
    private String promoTime;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}