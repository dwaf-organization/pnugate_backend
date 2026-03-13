package kr.ac.pnu.maingate.dto.resources.respDto;

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
public class ResourcesListRespDto {
    
    private Integer resourcesCode;
    private String resourcesTitle;
    private String resourcesWriter;
    private Integer resourcesViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}