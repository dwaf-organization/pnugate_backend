package kr.ac.pnu.maingate.service;

import kr.ac.pnu.maingate.common.dto.PageInfo;
import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeCreateReqDto;
import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeFileDto;
import kr.ac.pnu.maingate.dto.notice.reqDto.NoticeUpdateReqDto;
import kr.ac.pnu.maingate.dto.notice.respDto.NoticeDetailRespDto;
import kr.ac.pnu.maingate.dto.notice.respDto.NoticeListRespDto;
import kr.ac.pnu.maingate.dto.notice.respDto.NoticePageRespDto;
import kr.ac.pnu.maingate.entity.Notice;
import kr.ac.pnu.maingate.entity.NoticeFile;
import kr.ac.pnu.maingate.repository.NoticeFileRepository;
import kr.ac.pnu.maingate.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    
    /**
     * 공지사항 생성
     */
    @Transactional
    public Integer createNotice(NoticeCreateReqDto dto) {
        // 1. Notice 엔티티 생성
        Notice notice = Notice.builder()
                .noticeTitle(dto.getNoticeTitle())
                .noticeWriter(dto.getNoticeWriter())
                .noticeContents(dto.getNoticeContents())
                .noticeViews(0)
                .isDeleted(false)
                .build();
        
        // 2. Notice 저장
        Notice savedNotice = noticeRepository.save(notice);
        
        // 3. 파일이 있으면 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<NoticeFile> files = dto.getFiles().stream()
                    .map(fileDto -> NoticeFile.builder()
                            .notice(savedNotice)
                            .noticeFileName(fileDto.getNoticeFileName())
                            .noticeFileLink(fileDto.getNoticeFileLink())
                            .build())
                    .collect(Collectors.toList());
            
            noticeFileRepository.saveAll(files);
        }
        
        log.info("공지사항 생성: noticeCode={}, title={}", savedNotice.getNoticeCode(), savedNotice.getNoticeTitle());
        
        return savedNotice.getNoticeCode();
    }
    
    /**
     * 공지사항 목록 조회 (페이징)
     * @param page 프론트에서 받은 페이지 번호 (1부터 시작)
     */
    public NoticePageRespDto getNoticeList(Integer page, Integer size, String sort) {
        // 1. 정렬 기준 설정
        Sort sortBy;
        if ("views".equalsIgnoreCase(sort)) {
            // 조회수 내림차순
            sortBy = Sort.by(Sort.Direction.DESC, "noticeViews");
        } else {
            // 기본: noticeCode 내림차순 (최신순)
            sortBy = Sort.by(Sort.Direction.DESC, "noticeCode");
        }
        
        // 2. 페이징 설정 (프론트에서 1부터 시작하므로 -1)
        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        
        // 3. 조회
        Page<Notice> noticePage = noticeRepository.findByIsDeletedFalse(pageable);
        
        // 4. DTO 변환
        List<NoticeListRespDto> notices = noticePage.getContent().stream()
                .map(notice -> NoticeListRespDto.builder()
                        .noticeCode(notice.getNoticeCode())
                        .noticeTitle(notice.getNoticeTitle())
                        .noticeWriter(notice.getNoticeWriter())
                        .noticeViews(notice.getNoticeViews())
                        .createdAt(notice.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        // 5. 페이징 정보 (프론트가 보낸 page 값 그대로)
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .totalPages(noticePage.getTotalPages())
                .size(noticePage.getSize())
                .hasNext(noticePage.hasNext())
                .hasPrevious(noticePage.hasPrevious())
                .build();
        
        return NoticePageRespDto.builder()
                .notices(notices)
                .pageInfo(pageInfo)
                .build();
    }
    
    /**
     * 공지사항 상세 조회 (조회수 증가)
     */
    @Transactional
    public NoticeDetailRespDto getNoticeDetail(Integer noticeCode) {
        // 1. 공지사항 조회
        Notice notice = noticeRepository.findByNoticeCodeAndIsDeletedFalse(noticeCode)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        
        // 2. 조회수 증가
        noticeRepository.incrementViews(noticeCode);
        
        // 3. 파일 목록 변환
        List<NoticeFileDto> files = notice.getFiles().stream()
                .map(file -> NoticeFileDto.builder()
                        .noticeFileName(file.getNoticeFileName())
                        .noticeFileLink(file.getNoticeFileLink())
                        .build())
                .collect(Collectors.toList());
        
        // 4. DTO 변환
        return NoticeDetailRespDto.builder()
                .noticeCode(notice.getNoticeCode())
                .noticeTitle(notice.getNoticeTitle())
                .noticeWriter(notice.getNoticeWriter())
                .noticeContents(notice.getNoticeContents())
                .noticeViews(notice.getNoticeViews() + 1)  // 증가된 조회수
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .files(files)
                .build();
    }
    
    /**
     * 공지사항 수정
     */
    @Transactional
    public void updateNotice(NoticeUpdateReqDto dto) {
        // 1. 공지사항 조회
        Notice notice = noticeRepository.findByNoticeCodeAndIsDeletedFalse(dto.getNoticeCode())
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        
        // 2. 기본 정보 수정
        notice.setNoticeTitle(dto.getNoticeTitle());
        notice.setNoticeWriter(dto.getNoticeWriter());
        notice.setNoticeContents(dto.getNoticeContents());
        
        // 3. 파일 처리: 기존 파일 전체 삭제 후 재생성
        if (dto.getFiles() != null) {
            // 기존 파일 삭제
            noticeFileRepository.deleteByNoticeCode(dto.getNoticeCode());
            
            // 새 파일 저장
            if (!dto.getFiles().isEmpty()) {
                List<NoticeFile> newFiles = dto.getFiles().stream()
                        .map(fileDto -> NoticeFile.builder()
                                .notice(notice)
                                .noticeFileName(fileDto.getNoticeFileName())
                                .noticeFileLink(fileDto.getNoticeFileLink())
                                .build())
                        .collect(Collectors.toList());
                
                noticeFileRepository.saveAll(newFiles);
            }
        }
        
        log.info("공지사항 수정: noticeCode={}, title={}", dto.getNoticeCode(), dto.getNoticeTitle());
    }
    
    /**
     * 공지사항 삭제 (소프트 딜리트)
     */
    @Transactional
    public void deleteNotice(Integer noticeCode) {
        // 1. 공지사항 조회
        Notice notice = noticeRepository.findByNoticeCodeAndIsDeletedFalse(noticeCode)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        
        // 2. 소프트 딜리트
        notice.setIsDeleted(true);
        notice.setDeletedAt(LocalDateTime.now());
        
        log.info("공지사항 삭제: noticeCode={}, title={}", noticeCode, notice.getNoticeTitle());
    }
}