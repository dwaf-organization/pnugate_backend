package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFile, Integer> {
    
    /**
     * 특정 게시글의 모든 파일 삭제
     */
    @Modifying
    @Query("DELETE FROM BoardFile bf WHERE bf.board.boardCode = :boardCode")
    void deleteByBoardCode(@Param("boardCode") Integer boardCode);
}