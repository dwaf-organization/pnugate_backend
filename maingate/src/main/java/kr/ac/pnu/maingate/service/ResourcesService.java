package kr.ac.pnu.maingate.service;

import kr.ac.pnu.maingate.common.dto.PageInfo;
import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesCreateReqDto;
import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesFileDto;
import kr.ac.pnu.maingate.dto.resources.reqDto.ResourcesUpdateReqDto;
import kr.ac.pnu.maingate.dto.resources.respDto.ResourcesDetailRespDto;
import kr.ac.pnu.maingate.dto.resources.respDto.ResourcesListRespDto;
import kr.ac.pnu.maingate.dto.resources.respDto.ResourcesPageRespDto;
import kr.ac.pnu.maingate.entity.Resources;
import kr.ac.pnu.maingate.entity.ResourcesFile;
import kr.ac.pnu.maingate.repository.ResourcesFileRepository;
import kr.ac.pnu.maingate.repository.ResourcesRepository;
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
public class ResourcesService {
    
    private final ResourcesRepository resourcesRepository;
    private final ResourcesFileRepository resourcesFileRepository;
    
    /**
     * 자료실 생성
     */
    @Transactional
    public Integer createResources(ResourcesCreateReqDto dto) {
        // 1. Resources 엔티티 생성
        Resources resources = Resources.builder()
                .resourcesTitle(dto.getResourcesTitle())
                .resourcesWriter(dto.getResourcesWriter())
                .resourcesContents(dto.getResourcesContents())
                .resourcesViews(0)
                .isDeleted(false)
                .build();
        
        // 2. Resources 저장
        Resources savedResources = resourcesRepository.save(resources);
        
        // 3. 파일이 있으면 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<ResourcesFile> files = dto.getFiles().stream()
                    .map(fileDto -> ResourcesFile.builder()
                            .resources(savedResources)
                            .resourcesFileName(fileDto.getResourcesFileName())
                            .resourcesFileLink(fileDto.getResourcesFileLink())
                            .build())
                    .collect(Collectors.toList());
            
            resourcesFileRepository.saveAll(files);
        }
        
        log.info("자료실 생성: resourcesCode={}, title={}", savedResources.getResourcesCode(), savedResources.getResourcesTitle());
        
        return savedResources.getResourcesCode();
    }
    
    /**
     * 자료실 목록 조회 (페이징)
     * @param page 프론트에서 받은 페이지 번호 (1부터 시작)
     */
    public ResourcesPageRespDto getResourcesList(Integer page, Integer size, String sort) {
        // 1. 정렬 기준 설정
        Sort sortBy;
        if ("views".equalsIgnoreCase(sort)) {
            // 조회수 내림차순
            sortBy = Sort.by(Sort.Direction.DESC, "resourcesViews");
        } else {
            // 기본: resourcesCode 내림차순 (최신순)
            sortBy = Sort.by(Sort.Direction.DESC, "resourcesCode");
        }
        
        // 2. 페이징 설정 (프론트에서 1부터 시작하므로 -1)
        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        
        // 3. 조회
        Page<Resources> resourcesPage = resourcesRepository.findByIsDeletedFalse(pageable);
        
        // 4. DTO 변환
        List<ResourcesListRespDto> resources = resourcesPage.getContent().stream()
                .map(resource -> ResourcesListRespDto.builder()
                        .resourcesCode(resource.getResourcesCode())
                        .resourcesTitle(resource.getResourcesTitle())
                        .resourcesWriter(resource.getResourcesWriter())
                        .resourcesViews(resource.getResourcesViews())
                        .createdAt(resource.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        // 5. 페이징 정보 (프론트가 보낸 page 값 그대로)
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .totalPages(resourcesPage.getTotalPages())
                .size(resourcesPage.getSize())
                .hasNext(resourcesPage.hasNext())
                .hasPrevious(resourcesPage.hasPrevious())
                .build();
        
        return ResourcesPageRespDto.builder()
                .resources(resources)
                .pageInfo(pageInfo)
                .build();
    }
    
    /**
     * 자료실 상세 조회 (조회수 증가)
     */
    @Transactional
    public ResourcesDetailRespDto getResourcesDetail(Integer resourcesCode) {
        // 1. 자료실 조회
        Resources resources = resourcesRepository.findByResourcesCodeAndIsDeletedFalse(resourcesCode)
                .orElseThrow(() -> new IllegalArgumentException("자료실을 찾을 수 없습니다."));
        
        // 2. 조회수 증가
        resourcesRepository.incrementViews(resourcesCode);
        
        // 3. 파일 목록 변환
        List<ResourcesFileDto> files = resources.getFiles().stream()
                .map(file -> ResourcesFileDto.builder()
                        .resourcesFileName(file.getResourcesFileName())
                        .resourcesFileLink(file.getResourcesFileLink())
                        .build())
                .collect(Collectors.toList());
        
        // 4. DTO 변환
        return ResourcesDetailRespDto.builder()
                .resourcesCode(resources.getResourcesCode())
                .resourcesTitle(resources.getResourcesTitle())
                .resourcesWriter(resources.getResourcesWriter())
                .resourcesContents(resources.getResourcesContents())
                .resourcesViews(resources.getResourcesViews() + 1)  // 증가된 조회수
                .createdAt(resources.getCreatedAt())
                .updatedAt(resources.getUpdatedAt())
                .files(files)
                .build();
    }
    
    /**
     * 자료실 수정
     */
    @Transactional
    public void updateResources(ResourcesUpdateReqDto dto) {
        // 1. 자료실 조회
        Resources resources = resourcesRepository.findByResourcesCodeAndIsDeletedFalse(dto.getResourcesCode())
                .orElseThrow(() -> new IllegalArgumentException("자료실을 찾을 수 없습니다."));
        
        // 2. 기본 정보 수정
        resources.setResourcesTitle(dto.getResourcesTitle());
        resources.setResourcesWriter(dto.getResourcesWriter());
        resources.setResourcesContents(dto.getResourcesContents());
        
        // 3. 파일 처리: 기존 파일 전체 삭제 후 재생성
        if (dto.getFiles() != null) {
            // 기존 파일 삭제
            resourcesFileRepository.deleteByResourcesCode(dto.getResourcesCode());
            
            // 새 파일 저장
            if (!dto.getFiles().isEmpty()) {
                List<ResourcesFile> newFiles = dto.getFiles().stream()
                        .map(fileDto -> ResourcesFile.builder()
                                .resources(resources)
                                .resourcesFileName(fileDto.getResourcesFileName())
                                .resourcesFileLink(fileDto.getResourcesFileLink())
                                .build())
                        .collect(Collectors.toList());
                
                resourcesFileRepository.saveAll(newFiles);
            }
        }
        
        log.info("자료실 수정: resourcesCode={}, title={}", dto.getResourcesCode(), dto.getResourcesTitle());
    }
    
    /**
     * 자료실 삭제 (소프트 딜리트)
     */
    @Transactional
    public void deleteResources(Integer resourcesCode) {
        // 1. 자료실 조회
        Resources resources = resourcesRepository.findByResourcesCodeAndIsDeletedFalse(resourcesCode)
                .orElseThrow(() -> new IllegalArgumentException("자료실을 찾을 수 없습니다."));
        
        // 2. 소프트 딜리트
        resources.setIsDeleted(true);
        resources.setDeletedAt(LocalDateTime.now());
        
        log.info("자료실 삭제: resourcesCode={}, title={}", resourcesCode, resources.getResourcesTitle());
    }
}