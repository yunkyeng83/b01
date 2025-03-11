package org.zerock.b01.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity // 해당 클래스는 JPA에서 관리하는 entity로 지정
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString(exclude = "imageSet")
public class Board extends BaseEntity {
    @Id // PK(primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 숫자를 증가시켜줌. Auto Increment == Oracle Sequence
    private Long bno;  // 게시물 번호
    @Column(length = 500, nullable = false)
    private String title; // 게시물 제목
    @Column(length = 2000, nullable = false)
    private String content; // 게시물 내용
    @Column(length = 50, nullable = false)
    private String writer;  // 게시물 작성자

    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.LAZY  // 생략가능(기본이 LAZY)
    )
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

//    @OneToMany(mappedBy = "board")
//    @Builder.Default
//    private Set<Reply> replySet = new HashSet<>();

    // 게시물에 파라미터에 맞는 첨부파일 이미지 추가
    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    // 게시물에 있는 모든 첨부파일 이미지를 삭제
    public void clearImages() {
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();;
    }

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
