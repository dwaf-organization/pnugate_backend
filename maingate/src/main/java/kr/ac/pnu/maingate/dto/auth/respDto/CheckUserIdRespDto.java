package kr.ac.pnu.maingate.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserIdRespDto {
    
    private Boolean available;  // 사용 가능 여부
}
