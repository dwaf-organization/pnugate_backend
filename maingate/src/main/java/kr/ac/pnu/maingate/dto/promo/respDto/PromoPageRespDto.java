package kr.ac.pnu.maingate.dto.promo.respDto;

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
public class PromoPageRespDto {
    
    private List<PromoListRespDto> promos;
    private PageInfo pageInfo;
}