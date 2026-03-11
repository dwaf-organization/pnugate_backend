package kr.ac.pnu.maingate.service;

import kr.ac.pnu.maingate.dto.auth.reqDto.LoginReqDto;
import kr.ac.pnu.maingate.dto.auth.reqDto.SignupReqDto;
import kr.ac.pnu.maingate.dto.auth.respDto.CheckUserIdRespDto;
import kr.ac.pnu.maingate.dto.auth.respDto.LoginRespDto;
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