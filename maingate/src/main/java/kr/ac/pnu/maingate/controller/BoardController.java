package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.board.reqDto.BoardCreateReqDto;
import kr.ac.pnu.maingate.dto.board.reqDto.BoardUpdateReqDto;
import kr.ac.pnu.maingate.dto.board.respDto.BoardDetailRespDto;
import kr.ac.pnu.maingate.dto.board.respDto.BoardPageRespDto;
import kr.ac.pnu.maingate.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {
    
    private final BoardService boardService;
    
    /**
     * 게시글 생성
     * POST /api/v1/board/create
     */
    @PostMapping("/create")
    public ResponseEntity<RespDto<Integer>> createBoard(
            @Valid @RequestBody BoardCreateReqDto dto,
            BindingResult bindingResult,
            Authentication authentication) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            // JWT에서 userCode 추출
            Integer userCode = (Integer) authentication.getPrincipal();
            
            Integer boardCode = boardService.createBoard(userCode, dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("게시글이 등록되었습니다.", boardCode));
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 생성 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("게시글 등록 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 게시글 목록 조회 (페이징)
     * GET /api/v1/board/list?page=1&size=10&sort=latest
     */
    @GetMapping("/list")
    public ResponseEntity<RespDto<BoardPageRespDto>> getBoardList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", defaultValue = "latest") String sort) {
        
        try {
            BoardPageRespDto response = boardService.getBoardList(page, size, sort);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 게시글 상세 조회
     * GET /api/v1/board/detail/{boardCode}
     */
    @GetMapping("/detail/{boardCode}")
    public ResponseEntity<RespDto<BoardDetailRespDto>> getBoardDetail(
            @PathVariable("boardCode") Integer boardCode) {
        
        try {
            BoardDetailRespDto response = boardService.getBoardDetail(boardCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 게시글 수정 (작성자 본인만)
     * PUT /api/v1/board/update
     */
    @PutMapping("/update")
    public ResponseEntity<RespDto<Void>> updateBoard(
            @Valid @RequestBody BoardUpdateReqDto dto,
            BindingResult bindingResult,
            Authentication authentication) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            // JWT에서 userCode 추출
            Integer userCode = (Integer) authentication.getPrincipal();
            
            boardService.updateBoard(userCode, dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("게시글이 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("게시글 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 게시글 삭제 (작성자 본인만, 소프트 딜리트)
     * DELETE /api/v1/board/delete/{boardCode}
     */
    @DeleteMapping("/delete/{boardCode}")
    public ResponseEntity<RespDto<Void>> deleteBoard(
            @PathVariable("boardCode") Integer boardCode,
            Authentication authentication) {
        
        try {
            // JWT에서 userCode 추출
            Integer userCode = (Integer) authentication.getPrincipal();
            
            boardService.deleteBoard(userCode, boardCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("게시글이 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("게시글 삭제 중 오류가 발생했습니다."));
        }
    }
}