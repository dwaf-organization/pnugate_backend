package kr.ac.pnu.maingate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.ac.pnu.maingate.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 아이디로 사용자 조회
     */
    Optional<User> findByUserId(String userId);
    
    /**
     * 아이디 존재 여부 확인
     */
    boolean existsByUserId(String userId);
    
    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 아이디와 삭제되지 않은 사용자 조회
     */
    Optional<User> findByUserIdAndIsDeletedFalse(String userId);
}
