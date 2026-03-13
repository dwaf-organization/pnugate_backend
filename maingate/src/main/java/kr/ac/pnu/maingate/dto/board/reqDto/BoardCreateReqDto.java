package kr.ac.pnu.maingate.dto.board.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateReqDto {
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String boardTitle;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String boardContents;
    
    private List<BoardFileDto> files;  // 파일 목록 (선택)
}