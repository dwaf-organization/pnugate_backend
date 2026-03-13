package kr.ac.pnu.maingate.dto.board.respDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.ac.pnu.maingate.dto.board.reqDto.BoardFileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailRespDto {
    
    private Integer boardCode;
    private Integer userCode;  // 작성자 코드 (수정/삭제 권한 확인용)
    private String boardWriter;  // 작성자 이름
    private String boardTitle;
    private String boardContents;
    private Integer boardViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;
    
    private List<BoardFileDto> files;
}