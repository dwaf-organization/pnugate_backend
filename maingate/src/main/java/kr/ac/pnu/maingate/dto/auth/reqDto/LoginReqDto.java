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
public class LoginReqDto {
    
    @NotBlank(message = "아이디는 필수입니다.")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String userPw;
}