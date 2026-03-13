package kr.ac.pnu.maingate.dto.auth.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReqDto {
    
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}