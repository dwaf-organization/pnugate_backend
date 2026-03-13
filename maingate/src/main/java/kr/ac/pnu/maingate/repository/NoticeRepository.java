package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    
    /**
     * 삭제되지 않은 공지사항 페이징 조회
     */
    Page<Notice> findByIsDeletedFalse(Pageable pageable);
    
    /**
     * 삭제되지 않은 공지사항 단건 조회
     */
    Optional<Notice> findByNoticeCodeAndIsDeletedFalse(Integer noticeCode);
    
    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Notice n SET n.noticeViews = n.noticeViews + 1 WHERE n.noticeCode = :noticeCode")
    void incrementViews(@Param("noticeCode") Integer noticeCode);
}