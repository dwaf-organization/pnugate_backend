package kr.ac.pnu.maingate.service;

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
import kr.ac.pnu.maingate.entity.User;
import kr.ac.pnu.maingate.repository.UserRepository;
import kr.ac.pnu.maingate.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    private static final String INITIAL_PASSWORD = "a1234567";
    
    /**
     * 로그인
     */
    public LoginRespDto login(LoginReqDto dto) {
        // 1. 사용자 조회 (삭제되지 않은 사용자만)
        User user = userRepository.findByUserIdAndIsDeletedFalse(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getUserPw(), user.getUserPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(
                user.getUserCode(),
                user.getUserId(),
                user.getUserName()
        );
        
        String refreshToken = jwtUtil.createRefreshToken(
                user.getUserCode(),
                user.getUserId()
        );
        
        log.info("로그인 성공: userId={}, userCode={}", user.getUserId(), user.getUserCode());
        
        // 4. Response DTO 반환
        return LoginRespDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userCode(user.getUserCode())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 아이디 찾기
     */
    public String findUserId(FindIdReqDto dto) {
        // 1. 이름과 이메일로 사용자 조회
        User user = userRepository.findByUserNameAndEmailAndIsDeletedFalse(dto.getUserName(), dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자를 찾을 수 없습니다."));
        
        // 2. 아이디 마스킹 처리
        String maskedUserId = maskUserId(user.getUserId());
        
        log.info("아이디 찾기 성공: userName={}, email={}, maskedUserId={}", 
                dto.getUserName(), dto.getEmail(), maskedUserId);
        
        // 3. Response DTO 반환
        return user.getUserId();
    }

    /**
     * 비밀번호 찾기 (초기화)
     */
    @Transactional
    public ResetPasswordRespDto resetPassword(ResetPasswordReqDto dto) {
        // 1. 아이디 + 이름 + 이메일로 사용자 조회
        User user = userRepository.findByUserIdAndUserNameAndEmailAndIsDeletedFalse(
                dto.getUserId(), dto.getUserName(), dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
        
        // 2. 비밀번호를 초기화 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(INITIAL_PASSWORD);
        user.setUserPw(encodedPassword);
        
        log.info("비밀번호 초기화: userId={}, userCode={}", user.getUserId(), user.getUserCode());
        
        // 3. Response DTO 반환
        return ResetPasswordRespDto.builder()
                .initialPassword(INITIAL_PASSWORD)
                .build();
    }
    
    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Integer userCode, ChangePasswordReqDto dto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserCodeAndIsDeletedFalse(userCode)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        
        // 3. 비밀번호 변경
        user.setUserPw(encodedPassword);
        
        log.info("비밀번호 변경 성공: userId={}, userCode={}", user.getUserId(), userCode);
    }
    
    /**
     * 토큰 재설정 (Refresh Token으로 Access Token 재발급)
     */
    public RefreshTokenRespDto refreshAccessToken(RefreshTokenReqDto dto) {
        // 1. Refresh Token 검증
        if (!jwtUtil.validateToken(dto.getRefreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        
        // 2. Refresh Token에서 사용자 정보 추출
        Integer userCode = jwtUtil.getUserCode(dto.getRefreshToken());
        String userId = jwtUtil.getUserId(dto.getRefreshToken());
        
        // 3. 사용자 조회 (삭제되지 않은 사용자만)
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(
                user.getUserCode(),
                user.getUserId(),
                user.getUserName()
        );
        
        log.info("Access Token 재발급: userId={}, userCode={}", userId, userCode);
        
        // 5. Response DTO 반환
        return RefreshTokenRespDto.builder()
                .accessToken(newAccessToken)
                .build();
    }
    
    /**
     * 아이디 마스킹 처리
     * 예: hong123 → hon***3
     */
    private String maskUserId(String userId) {
        if (userId.length() <= 4) {
            // 4글자 이하면 첫 글자만 보이고 나머지 *
            return userId.charAt(0) + "***";
        }
        
        // 5글자 이상이면 앞 3글자 + *** + 마지막 1글자
        String prefix = userId.substring(0, 3);
        String suffix = userId.substring(userId.length() - 1);
        return prefix + "***" + suffix;
    }
    
    /**
     * 회원가입
     */
    @Transactional
    public SignupRespDto signup(SignupReqDto dto) {
        // 1. 아이디 중복 확인
        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        
        // 2. 이메일 중복 확인 (이메일이 있는 경우만)
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getUserPw());
        
        // 4. User 엔티티 생성
        User user = User.builder()
                .userId(dto.getUserId())
                .userPw(encodedPassword)
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .title(dto.getTitle())
                .profile(dto.getProfile())
                .provider(dto.getProvider())
                .isDeleted(false)
                .build();
        
        // 5. 저장
        User savedUser = userRepository.save(user);
        
        log.info("회원가입 성공: userId={}, userCode={}", savedUser.getUserId(), savedUser.getUserCode());
        
        // 6. Response DTO 변환
        return SignupRespDto.builder()
                .userCode(savedUser.getUserCode())
                .userId(savedUser.getUserId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
    
    /**
     * 아이디 중복 확인
     */
    public Boolean checkUserId(String userId) {
        boolean exists = userRepository.existsByUserId(userId);
        
        log.debug("아이디 중복 확인: userId={}, exists={}", userId, exists);
        
        return !exists;  // 존재하지 않으면 true (사용 가능)
    }
}