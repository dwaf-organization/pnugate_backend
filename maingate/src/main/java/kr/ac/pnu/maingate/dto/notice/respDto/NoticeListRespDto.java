package kr.ac.pnu.maingate.dto.notice.respDto;

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
public class NoticeListRespDto {
    
    private Integer noticeCode;
    private String noticeTitle;
    private String noticeWriter;
    private Integer noticeViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}