package kr.ac.pnu.maingate.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRespDto {
    
    private Integer userCode;
    private String userId;
    private String userName;
    private String email;
    private LocalDateTime createdAt;
}
