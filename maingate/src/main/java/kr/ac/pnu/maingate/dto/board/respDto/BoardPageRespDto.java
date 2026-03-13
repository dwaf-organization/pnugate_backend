package kr.ac.pnu.maingate.dto.board.respDto;

import kr.ac.pnu.maingate.common.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageRespDto {
    
    private List<BoardListRespDto> boards;
    private PageInfo pageInfo;
}