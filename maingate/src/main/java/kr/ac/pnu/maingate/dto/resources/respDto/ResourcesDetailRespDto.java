package kr.ac.pnu.maingate.dto.resources.respDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesFileDto;
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
public class ResourcesDetailRespDto {
    
    private Integer resourcesCode;
    private String resourcesTitle;
    private String resourcesWriter;
    private String resourcesContents;
    private Integer resourcesViews;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;
    
    private List<ResourcesFileDto> files;
}