package kr.ac.pnu.maingate.dto.notice.respDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeFileDto;
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
public class NoticeDetailRespDto {
    
    private Integer noticeCode;
    private String noticeTitle;
    private String noticeWriter;
    private String noticeContents;
    private Integer noticeViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;
    
    private List<NoticeFileDto> files;
}