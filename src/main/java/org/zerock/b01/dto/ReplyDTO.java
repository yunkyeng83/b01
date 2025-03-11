package org.zerock.b01.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
    private Long rno;               // 댓글 번호
    @NotNull
    private Long bno;               // 댓글이 적힌 게시물 번호
    @NotEmpty
    private String replyText;       // 실제 댓글
    @NotEmpty
    private String replyer;         // 댓글 작성자
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate; // 댓글 등록일시
    @JsonIgnore
    private LocalDateTime modDate;  // 댓글 수정일시
}
