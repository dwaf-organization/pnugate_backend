package kr.ac.pnu.maingate.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRespDto {
    
    private String accessToken;  // 새로 발급된 Access Token
}