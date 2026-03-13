package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.ResourcesFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourcesFileRepository extends JpaRepository<ResourcesFile, Integer> {
    
    /**
     * 특정 자료실의 모든 파일 삭제
     */
    @Modifying
    @Query("DELETE FROM ResourcesFile rf WHERE rf.resources.resourcesCode = :resourcesCode")
    void deleteByResourcesCode(@Param("resourcesCode") Integer resourcesCode);
}