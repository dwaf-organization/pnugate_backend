package kr.ac.pnu.maingate.dto.notice.respDto;

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
public class NoticePageRespDto {
    
    private List<NoticeListRespDto> notices;
    private PageInfo pageInfo;
}