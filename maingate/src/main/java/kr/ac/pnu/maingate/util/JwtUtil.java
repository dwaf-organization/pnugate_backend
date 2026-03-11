package kr.ac.pnu.maingate.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    
    private final SecretKey key;
    private final long accessTokenExpire;
    private final long refreshTokenExpire;
    
    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expire}") long accessTokenExpire,
            @Value("${jwt.refresh-token-expire}") long refreshTokenExpire) {
        
        byte[] keyBytes = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpire = accessTokenExpire;
        this.refreshTokenExpire = refreshTokenExpire;
    }
    
    /**
     * Access Token 생성
     */
    public String createAccessToken(Integer userCode, String userId, String userName) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpire);
        
        return Jwts.builder()
                .subject(userId)
                .claim("userCode", userCode)
                .claim("userId", userId)
                .claim("userName", userName)
                .claim("tokenType", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    
    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Integer userCode, String userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpire);
        
        return Jwts.builder()
                .subject(userId)
                .claim("userCode", userCode)
                .claim("tokenType", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    
    /**
     * 토큰에서 Claims 추출
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }
    
    /**
     * 토큰에서 userCode 추출
     */
    public Integer getUserCode(String token) {
        return parseClaims(token).get("userCode", Integer.class);
    }
    
    /**
     * 토큰에서 userName 추출
     */
    public String getUserName(String token) {
        return parseClaims(token).get("userName", String.class);
    }
    
    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    
    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}