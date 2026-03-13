package kr.ac.pnu.maingate.dto.promo.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoUpdateReqDto {
    
    @NotNull(message = "홍보영상 코드는 필수입니다.")
    private Integer promoCode;
    
    private String thumbnailFile;  // 썸네일 파일 링크 (선택)
    
    @NotBlank(message = "영상 링크는 필수입니다.")
    @Size(max = 500, message = "영상 링크는 500자 이하여야 합니다.")
    private String promoLink;
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String promoTitle;
    
    @Size(max = 100, message = "채널명은 100자 이하여야 합니다.")
    private String promoChannel;
    
    @Size(max = 50, message = "영상 시간은 50자 이하여야 합니다.")
    private String promoTime;  // HH:mm:ss 형식
}