package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Integer> {
    
    /**
     * 특정 공지사항의 모든 파일 삭제
     */
    @Modifying
    @Query("DELETE FROM NoticeFile nf WHERE nf.notice.noticeCode = :noticeCode")
    void deleteByNoticeCode(@Param("noticeCode") Integer noticeCode);
}