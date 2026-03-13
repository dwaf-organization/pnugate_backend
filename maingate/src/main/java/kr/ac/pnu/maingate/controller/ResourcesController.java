package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesCreateReqDto;
import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesUpdateReqDto;
import kr.ac.pnu.maingate.dto.resources.respDto.ResourcesDetailRespDto;
import kr.ac.pnu.maingate.dto.resources.respDto.ResourcesPageRespDto;
import kr.ac.pnu.maingate.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourcesController {
    
    private final ResourcesService resourcesService;
    
    /**
     * 자료실 생성
     * POST /api/v1/resources/create
     */
    @PostMapping("/create")
    public ResponseEntity<RespDto<Integer>> createResources(
            @Valid @RequestBody ResourcesCreateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            Integer resourcesCode = resourcesService.createResources(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("자료실이 등록되었습니다.", resourcesCode));
            
        } catch (Exception e) {
            log.error("자료실 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("자료실 등록 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 자료실 목록 조회 (페이징)
     * GET /api/v1/resources/list?page=1&size=10&sort=latest
     */
    @GetMapping("/list")
    public ResponseEntity<RespDto<ResourcesPageRespDto>> getResourcesList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        
        try {
            ResourcesPageRespDto response = resourcesService.getResourcesList(page, size, sort);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (Exception e) {
            log.error("자료실 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 자료실 상세 조회
     * GET /api/v1/resources/detail/{resourcesCode}
     */
    @GetMapping("/detail/{resourcesCode}")
    public ResponseEntity<RespDto<ResourcesDetailRespDto>> getResourcesDetail(
            @PathVariable("resourcesCode") Integer resourcesCode) {
        
        try {
            ResourcesDetailRespDto response = resourcesService.getResourcesDetail(resourcesCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("자료실 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("자료실 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 자료실 수정
     * PUT /api/v1/resources/update
     */
    @PutMapping("/update")
    public ResponseEntity<RespDto<Void>> updateResources(
            @Valid @RequestBody ResourcesUpdateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            resourcesService.updateResources(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("자료실이 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("자료실 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("자료실 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("자료실 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 자료실 삭제 (소프트 딜리트)
     * DELETE /api/v1/resources/delete/{resourcesCode}
     */
    @DeleteMapping("/delete/{resourcesCode}")
    public ResponseEntity<RespDto<Void>> deleteResources(
            @PathVariable("resourcesCode") Integer resourcesCode) {
        
        try {
            resourcesService.deleteResources(resourcesCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("자료실이 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("자료실 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("자료실 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("자료실 삭제 중 오류가 발생했습니다."));
        }
    }
}