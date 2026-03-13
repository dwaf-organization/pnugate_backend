package kr.ac.pnu.maingate.dto.resources.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcesFileDto {
    
    private String resourcesFileName;
    private String resourcesFileLink;
}
 