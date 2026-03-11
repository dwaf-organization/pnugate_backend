package kr.ac.pnu.maingate.dto.auth.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDto {
    
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4-20자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 가능합니다.")
    private String userId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$", 
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String userPw;
    
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "이름은 2-10자여야 합니다.")
    private String userName;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;
    
    @Size(max = 50, message = "직책은 50자 이하여야 합니다.")
    private String title;
    
    @Size(max = 100, message = "주요 약력은 100자 이하여야 합니다.")
    private String profile;
    
    @NotBlank(message = "가입 경로는 필수입니다.")
    @Pattern(regexp = "^(LOCAL|KAKAO|GOOGLE)$", message = "가입 경로는 LOCAL, KAKAO, GOOGLE 중 하나여야 합니다.")
    private String provider;
}