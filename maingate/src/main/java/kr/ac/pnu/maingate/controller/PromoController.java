package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.promo.reqDto.PromoCreateReqDto;
import kr.ac.pnu.maingate.dto.promo.reqDto.PromoUpdateReqDto;
import kr.ac.pnu.maingate.dto.promo.respDto.PromoPageRespDto;
import kr.ac.pnu.maingate.service.PromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/promo")
@RequiredArgsConstructor
public class PromoController {
    
    private final PromoService promoService;
    
    /**
     * 홍보영상 생성
     * POST /api/v1/promo/create
     */
    @PostMapping("/create")
    public ResponseEntity<RespDto<Integer>> createPromo(
            @Valid @RequestBody PromoCreateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            Integer promoCode = promoService.createPromo(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("홍보영상이 등록되었습니다.", promoCode));
            
        } catch (Exception e) {
            log.error("홍보영상 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("홍보영상 등록 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 홍보영상 목록 조회 (페이징)
     * GET /api/v1/promo/list?page=1&size=12
     */
    @GetMapping("/list")
    public ResponseEntity<RespDto<PromoPageRespDto>> getPromoList(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "12") Integer size) {
        
        try {
            PromoPageRespDto response = promoService.getPromoList(page, size);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("조회 성공", response));
            
        } catch (Exception e) {
            log.error("홍보영상 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 홍보영상 수정
     * PUT /api/v1/promo/update
     */
    @PutMapping("/update")
    public ResponseEntity<RespDto<Void>> updatePromo(
            @Valid @RequestBody PromoUpdateReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            promoService.updatePromo(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("홍보영상이 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("홍보영상 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("홍보영상 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("홍보영상 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 홍보영상 삭제 (물리 삭제)
     * DELETE /api/v1/promo/delete/{promoCode}
     */
    @DeleteMapping("/delete/{promoCode}")
    public ResponseEntity<RespDto<Void>> deletePromo(
            @PathVariable("promoCode") Integer promoCode) {
        
        try {
            promoService.deletePromo(promoCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("홍보영상이 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("홍보영상 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("홍보영상 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("홍보영상 삭제 중 오류가 발생했습니다."));
        }
    }
}