package kr.ac.pnu.maingate.dto.notice.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NoticeUpdateReqDto {
    
    @NotNull(message = "공지사항 코드는 필수입니다.")
    private Integer noticeCode;
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String noticeTitle;
    
    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 50, message = "작성자는 50자 이하여야 합니다.")
    private String noticeWriter;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String noticeContents;
    
    private List<NoticeFileDto> files;  // 파일 목록 (선택)
}