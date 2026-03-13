package kr.ac.pnu.maingate.dto.board.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileDto {
    
    private String boardFileName;
    private String boardFileLink;
}