package kr.ac.pnu.maingate.dto.notice.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileDto {
    
    private String noticeFileName;
    private String noticeFileLink;
}