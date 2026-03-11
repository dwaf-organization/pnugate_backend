package kr.ac.pnu.maingate.controller;

import jakarta.validation.Valid;
import kr.ac.pnu.maingate.common.dto.RespDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.LoginReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.SignupReqDto;
import kr.ac.pnu.maingate.dto.auth.respDto.CheckUserIdRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.LoginRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.SignupRespDto;
import kr.ac.pnu.maingate.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RespDto<Boolean>> checkUserId(@PathVariable("userId") String userId) {
        try {
            Boolean available = authService.checkUserId(userId);
            
            if (available) {
                // 사용 가능
                return ResponseEntity.ok()
                    .body(RespDto.success("사용 가능한 아이디입니다.", true));
            } else {
                // 이미 사용 중 (fail에 data 직접 넣기)
                return ResponseEntity.ok()
                    .body(RespDto.<Boolean>builder()
                        .code(-1)
                        .message("이미 사용 중인 아이디입니다.")
                        .data(false)
                        .build());
            }
            
        } catch (Exception e) {
            log.error("아이디 중복 확인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespDto.fail("아이디 중복 확인 중 오류가 발생했습니다."));
        }
    }
}