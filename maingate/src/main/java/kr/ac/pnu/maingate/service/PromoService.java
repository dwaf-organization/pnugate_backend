package kr.ac.pnu.maingate.service;

import kr.ac.pnu.maingate.common.dto.PageInfo;
import kr.ac.pnu.maingate.dto.promo.reqDto.PromoCreateReqDto;
import kr.ac.pnu.maingate.dto.promo.reqDto.PromoUpdateReqDto;
import kr.ac.pnu.maingate.dto.promo.respDto.PromoListRespDto;
import kr.ac.pnu.maingate.dto.promo.respDto.PromoPageRespDto;
import kr.ac.pnu.maingate.entity.PromoMst;
import kr.ac.pnu.maingate.repository.PromoMstRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromoService {
    
    private final PromoMstRepository promoMstRepository;
    
    /**
     * 홍보영상 생성
     */
    @Transactional
    public Integer createPromo(PromoCreateReqDto dto) {
        // 1. PromoMst 엔티티 생성
        PromoMst promo = PromoMst.builder()
                .thumbnailFile(dto.getThumbnailFile())
                .promoLink(dto.getPromoLink())
                .promoTitle(dto.getPromoTitle())
                .promoChannel(dto.getPromoChannel())
                .promoTime(dto.getPromoTime())
                .build();
        
        // 2. 저장
        PromoMst savedPromo = promoMstRepository.save(promo);
        
        log.info("홍보영상 생성: promoCode={}, title={}", savedPromo.getPromoCode(), savedPromo.getPromoTitle());
        
        return savedPromo.getPromoCode();
    }
    
    /**
     * 홍보영상 목록 조회 (페이징)
     * @param page 프론트에서 받은 페이지 번호 (1부터 시작)
     */
    public PromoPageRespDto getPromoList(Integer page, Integer size) {
        // 1. 페이징 설정 (promoCode 내림차순 = 최신순)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "promoCode"));
        
        // 2. 조회
        Page<PromoMst> promoPage = promoMstRepository.findAll(pageable);
        
        // 3. DTO 변환
        List<PromoListRespDto> promos = promoPage.getContent().stream()
                .map(promo -> PromoListRespDto.builder()
                        .promoCode(promo.getPromoCode())
                        .thumbnailFile(promo.getThumbnailFile())
                        .promoLink(promo.getPromoLink())
                        .promoTitle(promo.getPromoTitle())
                        .promoChannel(promo.getPromoChannel())
                        .promoTime(promo.getPromoTime())
                        .createdAt(promo.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        // 4. 페이징 정보 (프론트가 보낸 page 값 그대로)
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .totalPages(promoPage.getTotalPages())
                .size(promoPage.getSize())
                .hasNext(promoPage.hasNext())
                .hasPrevious(promoPage.hasPrevious())
                .build();
        
        return PromoPageRespDto.builder()
                .promos(promos)
                .pageInfo(pageInfo)
                .build();
    }
    
    /**
     * 홍보영상 수정
     */
    @Transactional
    public void updatePromo(PromoUpdateReqDto dto) {
        // 1. 홍보영상 조회
        PromoMst promo = promoMstRepository.findById(dto.getPromoCode())
                .orElseThrow(() -> new IllegalArgumentException("홍보영상을 찾을 수 없습니다."));
        
        // 2. 정보 수정
        promo.setThumbnailFile(dto.getThumbnailFile());
        promo.setPromoLink(dto.getPromoLink());
        promo.setPromoTitle(dto.getPromoTitle());
        promo.setPromoChannel(dto.getPromoChannel());
        promo.setPromoTime(dto.getPromoTime());
        
        log.info("홍보영상 수정: promoCode={}, title={}", dto.getPromoCode(), dto.getPromoTitle());
    }
    
    /**
     * 홍보영상 삭제 (물리 삭제)
     */
    @Transactional
    public void deletePromo(Integer promoCode) {
        // 1. 홍보영상 조회
        PromoMst promo = promoMstRepository.findById(promoCode)
                .orElseThrow(() -> new IllegalArgumentException("홍보영상을 찾을 수 없습니다."));
        
        // 2. 물리 삭제
        promoMstRepository.delete(promo);
        
        log.info("홍보영상 삭제: promoCode={}, title={}", promoCode, promo.getPromoTitle());
    }
}