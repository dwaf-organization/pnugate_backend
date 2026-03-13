package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeCreateReqDto;
import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeUpdateReqDto;
import kr.ac.pnu.maingate.dto.notice.respDto.NoticeDetailRespDto;
import kr.ac.pnu.maingate.dto.notice.respDto.NoticePageRespDto;
import kr.ac.pnu.maingate.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    
    /**
     * 공지사항 생성
     * POST /api/v1/notice/create
     */
    @PostMapping("/create")
    public ResponseEntity<RespDto<Integer>> createNotice(
            @Valid @RequestBody NoticeCreateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            Integer noticeCode = noticeService.createNotice(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("공지사항이 등록되었습니다.", noticeCode));
            
        } catch (Exception e) {
            log.error("공지사항 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("공지사항 등록 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 공지사항 목록 조회 (페이징)
     * GET /api/v1/notice/list?page=1&size=10&sort=latest
     */
    @GetMapping("/list")
    public ResponseEntity<RespDto<NoticePageRespDto>> getNoticeList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        
        try {
            NoticePageRespDto response = noticeService.getNoticeList(page, size, sort);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (Exception e) {
            log.error("공지사항 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 공지사항 상세 조회
     * GET /api/v1/notice/detail/{noticeCode}
     */
    @GetMapping("/detail/{noticeCode}")
    public ResponseEntity<RespDto<NoticeDetailRespDto>> getNoticeDetail(
            @PathVariable("noticeCode") Integer noticeCode) {
        
        try {
            NoticeDetailRespDto response = noticeService.getNoticeDetail(noticeCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("공지사항 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 공지사항 수정
     * PUT /api/v1/notice/update
     */
    @PutMapping("/update")
    public ResponseEntity<RespDto<Void>> updateNotice(
            @Valid @RequestBody NoticeUpdateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            noticeService.updateNotice(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("공지사항이 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("공지사항 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("공지사항 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 공지사항 삭제 (소프트 딜리트)
     * DELETE /api/v1/notice/delete/{noticeCode}
     */
    @DeleteMapping("/delete/{noticeCode}")
    public ResponseEntity<RespDto<Void>> deleteNotice(
            @PathVariable("noticeCode") Integer noticeCode) {
        
        try {
            noticeService.deleteNotice(noticeCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("공지사항이 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("공지사항 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("공지사항 삭제 중 오류가 발생했습니다."));
        }
    }
}