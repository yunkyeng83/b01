package org.zerock.b01.repository.search;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardImageDTO;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardSearchImpl implements BoardSearch {
    private final EntityManager entityManager;

    /**
     * 게시글의 일부 리스트 정보
     * @param types
     * @param keyword
     * @param pageable
     * @return
     */
    // JPQL
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // JPQL로 작성 - 게시글 리스트를 조회하는 로직
        StringBuilder jpql = new StringBuilder("SELECT b FROM Board b WHERE b.bno > 0");

        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            jpql.append(" AND (");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        jpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        jpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        jpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    jpql.append(" OR ");
                }
            }
            jpql.append(")");
        }
        jpql.append(" ORDER BY bno DESC");

        // JPQL로 쿼리 생성
        TypedQuery<Board> query = entityManager.createQuery(jpql.toString(), Board.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(
                jpql.toString().replace("SELECT b", "SELECT COUNT(b)"), Long.class
        );

        // 파라미터 바인딩
        if ((types != null && types.length > 0) && keyword != null) {
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        // 페이징 처리
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 조회
        List<Board> list = query.getResultList();
        long count = countQuery.getSingleResult();

        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 게시글의 일부 정보 및 댓글 카운트 정보
     * @param types
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        // JPQL로 작성 - 게시글과 댓글 수를 함께 조회하는 로직
        StringBuilder jpql = new StringBuilder("SELECT new org.zerock.b01.dto.BoardListReplyCountDTO (");
        jpql.append("b.bno, b.title, b.writer, b.regDate, count(r)) ");
        jpql.append("FROM Board b LEFT JOIN Reply r ON r.board = b ");
        jpql.append("WHERE b.bno > 0 ");

        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            jpql.append(" AND (");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        jpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        jpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        jpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    jpql.append(" OR ");
                }
            }
            jpql.append(") ");
        }
        jpql.append(" GROUP BY b ");    // 게시글에 따른 댓글 카운트를 가져오기 위한 group by사용
        jpql.append("   ORDER BY b.bno DESC");
        // JPQL로 쿼리 생성
        TypedQuery<BoardListReplyCountDTO> query = entityManager.createQuery(jpql.toString(), BoardListReplyCountDTO.class);

        // JPQL로 카운트 쿼리 생성
        StringBuilder countJpql = new StringBuilder("SELECT count(distinct b) FROM Board b ");
        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            countJpql.append(" WHERE ");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        countJpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        countJpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        countJpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    countJpql.append(" OR ");
                }
            }
        }
        // JPQL로 쿼리 생성
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

        // 파라미터 바인딩
        if ((types != null && types.length > 0) && keyword != null) {
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        // 페이징 처리
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 조회
        List<BoardListReplyCountDTO> list = query.getResultList();
        long count = countQuery.getSingleResult();

        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 게시글의 일부 정보 및 댓글 카운트 및 이미지 미리보기 정보
     * @param types
     * @param keyword
     * @param pageable
     * @return
     */
    @Deprecated
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        // JPQL로 작성 - 게시글과 댓글 수, 이미지를 함께 조회하는 로직
        StringBuilder jpql = new StringBuilder("SELECT new org.zerock.b01.dto.BoardListAllDTO(");
        jpql.append("b.bno, b.title, b.writer, b.regDate, count(distinct r), ");
        jpql.append("GROUP_CONCAT(concat(bi.uuid,'_',bi.fileName,'_',bi.ord))) ");
        jpql.append("FROM Board b ");
        jpql.append("LEFT JOIN Reply r ON r.board = b ");
        jpql.append("LEFT JOIN b.imageSet bi ");
        jpql.append("WHERE b.bno > 0 ");

        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            jpql.append(" AND (");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        jpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        jpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        jpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    jpql.append(" OR ");
                }
            }
            jpql.append(") ");
        }
        jpql.append(" GROUP BY b ");    // 게시글에 따른 댓글 카운트를 가져오기 위한 group by사용
        jpql.append(" ORDER BY b.bno DESC");

        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);

        // JPQL로 카운트 쿼리 생성
        StringBuilder countJpql = new StringBuilder("SELECT count(distinct b) FROM Board b ");
        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            countJpql.append(" WHERE ");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        countJpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        countJpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        countJpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    countJpql.append(" OR ");
                }
            }
        }
        // JPQL로 쿼리 생성
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

        // 파라미터 바인딩
        if ((types != null && types.length > 0) && keyword != null) {
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        // 페이징 처리
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과를 BoardListAllDTO로 변환
        List<Object[]> resultList = query.getResultList();
        List<BoardListAllDTO> dtoList = resultList.stream().map(objects -> {
            BoardListAllDTO dto = new BoardListAllDTO();
            dto.setBno((Long)objects[0]);
            dto.setTitle((String)objects[1]);
            dto.setWriter((String)objects[2]);
            dto.setRegDate((LocalDateTime)objects[3]);
            dto.setReplyCount((Long)objects[4]);

            String imageInfo = (String)objects[5];
            if(imageInfo != null && Strings.isNotEmpty(imageInfo)) {
                List<BoardImageDTO> imageDTOS = Arrays.stream(imageInfo.split(","))
                        .map(str -> {
                            String[] parts = str.split("_");
                            BoardImageDTO boardImageDTO = BoardImageDTO.builder()
                                    .uuid(parts[0])
                                    .fileName(parts[1])
                                    .ord(Integer.parseInt(parts[2]))
                                    .build();
                            return boardImageDTO;
                        }).collect(Collectors.toList());
                dto.setBoardImages(imageDTOS);
            }
            return dto;
        }).collect(Collectors.toList());

        long totalCount = countQuery.getSingleResult();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    @Override
    public Page<BoardListAllDTO> searchWithAllNew(String[] types, String keyword, Pageable pageable) {
        // JPQL로 작성 - 게시글 리스트 조회하는 로직
        StringBuilder mainJpql = new StringBuilder("SELECT b.bno FROM Board b WHERE b.bno > 0");

        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            mainJpql.append(" AND (");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        mainJpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        mainJpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        mainJpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    mainJpql.append(" OR ");
                }
            }
            mainJpql.append(") ");
        }
        mainJpql.append(" ORDER BY b.bno DESC");

        // 페이징된 게시물 번호 목록 조회
        TypedQuery<Long> mainQuery = entityManager.createQuery(mainJpql.toString(), Long.class);
        // 파라미터 바인딩
        if ((types != null && types.length > 0) && keyword != null) {
            mainQuery.setParameter("keyword", "%" + keyword + "%");
        }
        // 페이징 처리
        mainQuery.setFirstResult((int) pageable.getOffset());
        mainQuery.setMaxResults(pageable.getPageSize());

        // 가져온 게시물 번호 리스트 저장(for 첨부파일 이미지 조회)
        List<Long> bnoList = mainQuery.getResultList();
        if (bnoList.isEmpty()) {    // 검색한 게시물이 없을 경우 메소드 종료
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // ------------------------------------------------------------------------------------------
        // 조회된 게시물의 첨부파일 정보 리스트를 조회
        // JPQL로 작성 - 게시글과 댓글 수, 이미지를 함께 조회하는 로직
        StringBuilder jpql = new StringBuilder("SELECT ");
        jpql.append("b.bno, b.title, b.writer, b.regDate, count(distinct r), bi.uuid, bi.fileName, bi.ord ");
        jpql.append("FROM Board b ");
        jpql.append("LEFT JOIN Reply r ON r.board = b ");
        jpql.append("LEFT JOIN b.imageSet bi ");
        jpql.append("WHERE b.bno IN :bnos ");
        jpql.append("GROUP BY b.bno, b.title, b.writer, b.regDate, bi.uuid, bi.fileName, bi.ord ");
        jpql.append("ORDER BY b.bno DESC");

        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        query.setParameter("bnos", bnoList);
        List<Object[]> resultList = query.getResultList();

        // 게시물별로 첨부파일 이미지를 그룹화하여 dto로 생성
        Map<Long, BoardListAllDTO> dtoMap = new HashMap<>();
        for(Object[] row: resultList) {
            Long bno = (Long)row[0];
            BoardListAllDTO dto = dtoMap.get(bno);

            if (dto == null) {
                dto = new BoardListAllDTO();
                // 기존의 게시물과 댓글 수 정보 저장
                dto.setBno(bno);
                dto.setTitle((String) row[1]);
                dto.setWriter((String) row[2]);
                dto.setRegDate((LocalDateTime) row[3]);
                dto.setReplyCount((Long) row[4]);
                dto.setBoardImages(new ArrayList<>());

                dtoMap.put(bno, dto);
            }

            // 게시물과 연계된 첨부파일 정보 저장(게시물에 첨부파일이 있는 경우에만 첨부파일 정보 저장)
            if(row[5] != null) {
                BoardImageDTO boardImageDTO = new BoardImageDTO();
                boardImageDTO.setUuid((String)row[5]);
                boardImageDTO.setFileName((String)row[6]);
                boardImageDTO.setOrd((Integer)row[7]);

                dto.getBoardImages().add(boardImageDTO);
            }
        }
        List<BoardListAllDTO> dtoList = new ArrayList<>(dtoMap.values());
        // 정렬된 결과를 유지하기 위해 다시 bno기준으로 다시 정렬
        dtoList.sort((a, b) -> b.getBno().compareTo(a.getBno()));

        // ------------------------------------------------------------------------------------------
        // JPQL로 카운트 쿼리 생성
        StringBuilder countJpql = new StringBuilder("SELECT count(distinct b) FROM Board b ");
        // 동적 검색 조건 추가
        if ((types != null && types.length > 0) && keyword != null) {
            countJpql.append(" WHERE ");

            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                switch (type) {
                    case "t":
                        countJpql.append("b.title LIKE :keyword");
                        break;
                    case "c":
                        countJpql.append("b.content LIKE :keyword");
                        break;
                    case "w":
                        countJpql.append("b.writer LIKE :keyword");
                        break;
                }

                // 각 조건 사이에 "OR" 추가
                if (i < types.length - 1) {
                    countJpql.append(" OR ");
                }
            }
        }
        // JPQL로 쿼리 생성
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);
        // 파라미터 바인딩
        if ((types != null && types.length > 0) && keyword != null) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        long totalCount = countQuery.getSingleResult();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}
