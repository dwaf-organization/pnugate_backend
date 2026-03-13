package kr.ac.pnu.maingate.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRespDto {
    
    private String initialPassword;  // 초기화된 비밀번호 (a1234567)
}