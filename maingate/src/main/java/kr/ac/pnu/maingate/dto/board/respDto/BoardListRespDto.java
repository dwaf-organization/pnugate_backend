package kr.ac.pnu.maingate.dto.board.respDto;

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
public class BoardListRespDto {
    
    private Integer boardCode;
    private String boardTitle;
    private String boardWriter;  // 작성자 이름
    private Integer boardViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}