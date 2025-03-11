package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    // jpa insert 기능 테스트
    @Test
    public void testJpaInsert() {
        // 100개의 임의 board객체를 생성
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." + i)
                    .content("content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            // jpa insert기능으로 board테이블에 데이터 저장
            Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    // jpa select 기능 테스트(1개 행 가져오기)
    @Test
    public void testSelect() {
        Optional<Board> result = boardRepository.findById(10L); // bno가 10인 행을 가져오기
        Board board = result.orElseThrow();

        log.info(board);
    }
    // jpa select 기능 테스트(전체 행 가져오기)
    @Test
    public void testSelectAll() {
        List<Board> result = boardRepository.findAll();

        result.forEach(item -> log.info(item));
    }

    // jpa update 기능 테스트
    @Test
    public void testUpdate() {
        Optional<Board> result = boardRepository.findById(10L); // bno가 10인 행을 가져오기
        Board board = result.orElseThrow();
        board.change("update..title 100", "update content 100");
        boardRepository.save(board);  // board라는 객체에 id값이 있기 때문에 해당 save는 update sql이 실생
    }

    // jpa delete 기능 테스트(id로 삭제하는 방법)
    @Test
    public void testDelete1() {

//        boardRepository.deleteById(1L);
    }

    // jpa delete 기능 테스트(객체로 삭제하는 방법)
    @Test
    public void testDelete2() {
//        Optional<Board> result = boardRepository.findById(9L); // bno가 9인 행을 가져오기
//        Board board = result.orElseThrow();
//
//        boardRepository.delete(board);
    }

    // JPA paging 기능 테스트
    @Test
    public void testPaging() {
        // 1 page에 게시물 번호 내림차순으로 정렬
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count: "+result.getTotalElements());
        log.info("total pages:" +result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());

        List<Board> todoList = result.getContent();

        todoList.forEach(board -> log.info(board));
    }

    // JPA Query Method 테스트1
    @Test
    public void queryMethodTest1() {
        List<Board> boardList
                = boardRepository.findByTitleAndWriter("title...99", "user9");

        boardList.forEach(board -> log.info(board));
    }
    // JPA Query Method 테스트2
    @Test
    public void queryMethodTest2() {
        List<Board> boardList
                = boardRepository.findByWriterIn(Arrays.asList("user9", "user3"));

        boardList.forEach(board -> log.info(board));
    }// JPA Query Method 테스트3
    @Test
    public void queryMethodTest3() {
        // 1 page에 게시물 번호 내림차순으로 정렬
        Pageable pageable
                = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> boardPage
                = boardRepository.findByTitleContainingOrderByBnoDesc("content", pageable);

        boardPage.forEach(board -> log.info(board));
    }

    @Test
    public void queryMethodAnnotationTest1() {
        // 1 page에 게시물 번호 내림차순으로 정렬
        Pageable pageable
            = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> boardPage
            = boardRepository.findKeyword("content", pageable);

        boardPage.forEach(board -> log.info(board));
    }

    @Test
    public void testInsertWithImages() {
        // 게시물
        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();
        // 게시물의 첨부파일
        for(int i = 0; i < 3; i++) {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }
        boardRepository.save(board);
    }

    /**
     * @EntityGraph 테스트
     */
    @Test
    //@Transactional // OneToMany에서 N+1문제가 발생
    public void testReadWithImages() {
        //Optional<Board> result = boardRepository.findById(1L); // lazy로 fetch
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
//        Board board = result.orElseThrow();

//        log.info(board);
//        log.info("-----------------");
//        log.info(board.getImageSet()); // error
    }

    /**
     * orphanRemoval 속성 테스트
     */
    @Transactional
    @Commit
    @Test
    public void testModifyImages() {
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
//        Board board = result.orElseThrow();
//
//        // 기존의 첨부파일들과 연계된 게시물 번호속성을 null로 세팅(orphanRemoval가 true일 경우 적용)
//        board.clearImages();
//
//        for(int i = 0; i < 2; i++) {
//            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
//        }
//
//        boardRepository.save(board);
    }

    /**
     * 게시글 삭제 및 관련 댓글 삭제 테스트
     */
    @Transactional
    @Commit
    @Test
    public void testRemoveAll() {
        Long bno = 1L;

        // 댓글 삭제(@ManyToOne에는 orphanRemoval속성 없어서 별도로 댓글 삭제 jpa호출)
        replyRepository.deleteByBoard_Bno(bno);
        // 게시글 삭제
//        boardRepository.deleteById(bno);
    }

    @Test
    public void testInsertAll() {
        // 게시물 100개 및 첨부파일 데이터 추가
        for(int i = 1; i <= 100; i++) {
            Board board = Board.builder()
                    .title("Title.. " + i)
                    .content("Content.. " + i)
                    .writer("Writer.. " + i)
                    .build();

            for(int j = 0; j < 3; j++) {
                if (i % 5 == 0) {     // 게시물이 5의 배수인 것들만 첨부파일 데이터 추가
                    board.addImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");
                }
            }
            boardRepository.save(board);
        }
    }
}
