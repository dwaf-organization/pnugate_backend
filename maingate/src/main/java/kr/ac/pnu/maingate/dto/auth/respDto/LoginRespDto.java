package kr.ac.pnu.maingate.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespDto {
    
    private String accessToken;
    private String refreshToken;
    
    // 사용자 정보
    private Integer userCode;
    private String userId;
    private String userName;
    private String email;
}