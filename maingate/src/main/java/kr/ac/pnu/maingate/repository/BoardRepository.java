package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    
    /**
     * 삭제되지 않은 게시글 페이징 조회
     */
    Page<Board> findByIsDeletedFalse(Pageable pageable);
    
    /**
     * 삭제되지 않은 게시글 단건 조회
     */
    Optional<Board> findByBoardCodeAndIsDeletedFalse(Integer boardCode);
    
    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Board b SET b.boardViews = b.boardViews + 1 WHERE b.boardCode = :boardCode")
    void incrementViews(@Param("boardCode") Integer boardCode);
}