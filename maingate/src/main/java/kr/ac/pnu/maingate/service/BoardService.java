package kr.ac.pnu.maingate.service;

import kr.ac.pnu.maingate.common.dto.PageInfo;
import kr.ac.pnu.maingate.dto.board.reqDto.BoardCreateReqDto;
import kr.ac.pnu.maingate.dto.board.reqDto.BoardFileDto;
import kr.ac.pnu.maingate.dto.board.reqDto.BoardUpdateReqDto;
import kr.ac.pnu.maingate.dto.board.respDto.BoardDetailRespDto;
import kr.ac.pnu.maingate.dto.board.respDto.BoardListRespDto;
import kr.ac.pnu.maingate.dto.board.respDto.BoardPageRespDto;
import kr.ac.pnu.maingate.entity.Board;
import kr.ac.pnu.maingate.entity.BoardFile;
import kr.ac.pnu.maingate.entity.User;
import kr.ac.pnu.maingate.repository.BoardFileRepository;
import kr.ac.pnu.maingate.repository.BoardRepository;
import kr.ac.pnu.maingate.repository.UserRepository;
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
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;
    
    /**
     * 게시글 생성
     */
    @Transactional
    public Integer createBoard(Integer userCode, BoardCreateReqDto dto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserCodeAndIsDeletedFalse(userCode)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. Board 엔티티 생성
        Board board = Board.builder()
                .user(user)
                .boardTitle(dto.getBoardTitle())
                .boardContents(dto.getBoardContents())
                .boardViews(0)
                .isDeleted(false)
                .build();
        
        // 3. Board 저장
        Board savedBoard = boardRepository.save(board);
        
        // 4. 파일이 있으면 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<BoardFile> files = dto.getFiles().stream()
                    .map(fileDto -> BoardFile.builder()
                            .board(savedBoard)
                            .boardFileName(fileDto.getBoardFileName())
                            .boardFileLink(fileDto.getBoardFileLink())
                            .build())
                    .collect(Collectors.toList());
            
            boardFileRepository.saveAll(files);
        }
        
        log.info("게시글 생성: boardCode={}, userCode={}, title={}", 
                savedBoard.getBoardCode(), userCode, savedBoard.getBoardTitle());
        
        return savedBoard.getBoardCode();
    }
    
    /**
     * 게시글 목록 조회 (페이징)
     * @param page 프론트에서 받은 페이지 번호 (1부터 시작)
     */
    public BoardPageRespDto getBoardList(Integer page, Integer size, String sort) {
        // 1. 정렬 기준 설정
        Sort sortBy;
        if ("views".equalsIgnoreCase(sort)) {
            // 조회수 내림차순
            sortBy = Sort.by(Sort.Direction.DESC, "boardViews");
        } else {
            // 기본: boardCode 내림차순 (최신순)
            sortBy = Sort.by(Sort.Direction.DESC, "boardCode");
        }
        
        // 2. 페이징 설정 (프론트에서 1부터 시작하므로 -1)
        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        
        // 3. 조회
        Page<Board> boardPage = boardRepository.findByIsDeletedFalse(pageable);
        
        // 4. DTO 변환
        List<BoardListRespDto> boards = boardPage.getContent().stream()
                .map(board -> BoardListRespDto.builder()
                        .boardCode(board.getBoardCode())
                        .boardTitle(board.getBoardTitle())
                        .boardWriter(board.getUser().getUserName())
                        .boardViews(board.getBoardViews())
                        .createdAt(board.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        // 5. 페이징 정보 (프론트가 보낸 page 값 그대로)
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(page)
                .totalPages(boardPage.getTotalPages())
                .size(boardPage.getSize())
                .hasNext(boardPage.hasNext())
                .hasPrevious(boardPage.hasPrevious())
                .build();
        
        return BoardPageRespDto.builder()
                .boards(boards)
                .pageInfo(pageInfo)
                .build();
    }
    
    /**
     * 게시글 상세 조회 (조회수 증가)
     */
    @Transactional
    public BoardDetailRespDto getBoardDetail(Integer boardCode) {
        // 1. 게시글 조회
        Board board = boardRepository.findByBoardCodeAndIsDeletedFalse(boardCode)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 2. 조회수 증가
        boardRepository.incrementViews(boardCode);
        
        // 3. 파일 목록 변환
        List<BoardFileDto> files = board.getFiles().stream()
                .map(file -> BoardFileDto.builder()
                        .boardFileName(file.getBoardFileName())
                        .boardFileLink(file.getBoardFileLink())
                        .build())
                .collect(Collectors.toList());
        
        // 4. DTO 변환
        return BoardDetailRespDto.builder()
                .boardCode(board.getBoardCode())
                .userCode(board.getUser().getUserCode())
                .boardWriter(board.getUser().getUserName())
                .boardTitle(board.getBoardTitle())
                .boardContents(board.getBoardContents())
                .boardViews(board.getBoardViews() + 1)  // 증가된 조회수
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .files(files)
                .build();
    }
    
    /**
     * 게시글 수정 (작성자 본인만 가능)
     */
    @Transactional
    public void updateBoard(Integer userCode, BoardUpdateReqDto dto) {
        // 1. 게시글 조회
        Board board = boardRepository.findByBoardCodeAndIsDeletedFalse(dto.getBoardCode())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 2. 작성자 본인 확인
        if (!board.getUser().getUserCode().equals(userCode)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }
        
        // 3. 기본 정보 수정
        board.setBoardTitle(dto.getBoardTitle());
        board.setBoardContents(dto.getBoardContents());
        
        // 4. 파일 처리: 기존 파일 전체 삭제 후 재생성
        if (dto.getFiles() != null) {
            // 기존 파일 삭제
            boardFileRepository.deleteByBoardCode(dto.getBoardCode());
            
            // 새 파일 저장
            if (!dto.getFiles().isEmpty()) {
                List<BoardFile> newFiles = dto.getFiles().stream()
                        .map(fileDto -> BoardFile.builder()
                                .board(board)
                                .boardFileName(fileDto.getBoardFileName())
                                .boardFileLink(fileDto.getBoardFileLink())
                                .build())
                        .collect(Collectors.toList());
                
                boardFileRepository.saveAll(newFiles);
            }
        }
        
        log.info("게시글 수정: boardCode={}, userCode={}, title={}", 
                dto.getBoardCode(), userCode, dto.getBoardTitle());
    }
    
    /**
     * 게시글 삭제 (작성자 본인만 가능, 소프트 딜리트)
     */
    @Transactional
    public void deleteBoard(Integer userCode, Integer boardCode) {
        // 1. 게시글 조회
        Board board = boardRepository.findByBoardCodeAndIsDeletedFalse(boardCode)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        // 2. 작성자 본인 확인
        if (!board.getUser().getUserCode().equals(userCode)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }
        
        // 3. 소프트 딜리트
        board.setIsDeleted(true);
        board.setDeletedAt(LocalDateTime.now());
        
        log.info("게시글 삭제: boardCode={}, userCode={}, title={}", 
                boardCode, userCode, board.getBoardTitle());
    }
}