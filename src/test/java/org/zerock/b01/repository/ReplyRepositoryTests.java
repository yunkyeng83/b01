package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.Reply;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {
    @Autowired
    private ReplyRepository replyRepository;

    /**
     * 댓글 등록 repository테스트
     */
    @Test
    public void testInsert() {
        // 실제 DB에 100번의 게시물이 존재해야 함
        Long bno = 100L;

        Board board = Board.builder()
                .bno(bno)   // board pk속성 세팅
                .build();

        Reply reply = Reply.builder()
                .replyText("댓글...")
                .replyer("replyer1")
                .board(board)
                .build();

        replyRepository.save(reply);
    }

    /**
     * 게시글에 대한 댓글 리스트 가져오기
     */
    @Test
    @Transactional  // board를 fetch해 올 때 lazy로 가져오기 때문에 해당 어노테이션 설정
    public void testBoardReplies() {
        Long bno = 100L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        result.getContent().forEach(reply -> {
            log.info(reply);
        });
    }
}
