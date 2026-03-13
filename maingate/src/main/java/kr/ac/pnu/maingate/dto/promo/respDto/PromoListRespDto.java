package kr.ac.pnu.maingate.dto.promo.respDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoListRespDto {
    
    private Integer promoCode;
    private String thumbnailFile;
    private String promoLink;
    private String promoTitle;
    private String promoChannel;
    private String promoTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}