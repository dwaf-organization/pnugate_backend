package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    
    /**
     * userCode로 삭제되지 않은 사용자 조회
     */
    Optional<User> findByUserCodeAndIsDeletedFalse(Integer userCode);
    
    /**
     * 이름과 이메일로 삭제되지 않은 사용자 조회 (아이디 찾기용)
     */
    Optional<User> findByUserNameAndEmailAndIsDeletedFalse(String userName, String email);
    
    /**
     * 아이디, 이름, 이메일로 삭제되지 않은 사용자 조회 (비밀번호 찾기용)
     */
    Optional<User> findByUserIdAndUserNameAndEmailAndIsDeletedFalse(String userId, String userName, String email);
}