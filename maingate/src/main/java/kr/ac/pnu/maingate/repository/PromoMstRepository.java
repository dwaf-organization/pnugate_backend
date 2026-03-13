package kr.ac.pnu.maingate.repository;

import kr.ac.pnu.maingate.entity.PromoMst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoMstRepository extends JpaRepository<PromoMst, Integer> {
    
    /**
     * 전체 홍보영상 페이징 조회
     */
    Page<PromoMst> findAll(Pageable pageable);
}