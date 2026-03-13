package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.ChangePasswordReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.FindIdReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.LoginReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.RefreshTokenReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.ResetPasswordReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.SignupReqDto;
import kr.ac.pnu.maingate.dto.auth.respDto.LoginRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.RefreshTokenRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.ResetPasswordRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.SignupRespDto;
import kr.ac.pnu.maingate.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<RespDto<LoginRespDto>> login(
            @Valid @RequestBody LoginReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            LoginRespDto response = authService.login(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("로그인 성공", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("로그인 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 아이디 찾기
     * POST /api/v1/auth/find-id
     */
    @PostMapping("/find-id")
    public ResponseEntity<RespDto<String>> findUserId(
            @Valid @RequestBody FindIdReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            String response = authService.findUserId(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("아이디를 찾았습니다.", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("아이디 찾기 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("아이디 찾기 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("아이디 찾기 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 비밀번호 찾기 (초기화)
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<RespDto<ResetPasswordRespDto>> resetPassword(
            @Valid @RequestBody ResetPasswordReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            ResetPasswordRespDto response = authService.resetPassword(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("비밀번호가 초기화되었습니다. 로그인 후 비밀번호를 변경해주세요.", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 초기화 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("비밀번호 초기화 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 비밀번호 변경
     * PUT /api/v1/auth/change-password
     */
    @PutMapping("/change-password")
    public ResponseEntity<RespDto<Void>> changePassword(
            @Valid @RequestBody ChangePasswordReqDto dto,
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
            
            authService.changePassword(userCode, dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("비밀번호가 변경되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 변경 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("비밀번호 변경 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 토큰 재설정 (Access Token 재발급)
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<RespDto<RefreshTokenRespDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            RefreshTokenRespDto response = authService.refreshAccessToken(dto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("토큰이 재발급되었습니다.", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("토큰 재발급 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("토큰 재발급 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 회원가입
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<RespDto<SignupRespDto>> signup(
            @Valid @RequestBody SignupReqDto dto,
            BindingResult bindingResult) {
        
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            SignupRespDto response = authService.signup(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("회원가입이 완료되었습니다.", response));
            
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("회원가입 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 아이디 중복 확인
     * GET /api/v1/auth/check-userid/{userId}
     */
    @GetMapping("/check-userid/{userId}")
    public ResponseEntity<RespDto<Boolean>> checkUserId(
            @PathVariable("userId") String userId) {
        
        try {
            Boolean available = authService.checkUserId(userId);
            
            if (available) {
                // 사용 가능
                return ResponseEntity
                        .ok()
                        .body(RespDto.success("사용 가능한 아이디입니다.", true));
            } else {
                // 이미 사용 중
                return ResponseEntity
                        .ok()
                        .body(RespDto.<Boolean>builder()
                                .code(-1)
                                .message("이미 사용 중인 아이디입니다.")
                                .data(false)
                                .build());
            }
            
        } catch (Exception e) {
            log.error("아이디 중복 확인 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("아이디 중복 확인 중 오류가 발생했습니다."));
        }
    }
}