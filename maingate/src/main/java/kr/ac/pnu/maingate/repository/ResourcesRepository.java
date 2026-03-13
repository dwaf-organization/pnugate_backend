package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.Resources;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Integer> {
    
    /**
     * 삭제되지 않은 자료실 페이징 조회
     */
    Page<Resources> findByIsDeletedFalse(Pageable pageable);
    
    /**
     * 삭제되지 않은 자료실 단건 조회
     */
    Optional<Resources> findByResourcesCodeAndIsDeletedFalse(Integer resourcesCode);
    
    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Resources r SET r.resourcesViews = r.resourcesViews + 1 WHERE r.resourcesCode = :resourcesCode")
    void incrementViews(@Param("resourcesCode") Integer resourcesCode);
}