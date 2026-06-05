package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 정보 VO.
 * 가이드의 댓글 및 대댓글 트리 구조를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentVo {
    private Long commentSeq;
    private Long parentSeq;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private boolean isMine;
    private List<CommentVo> replies = new ArrayList<>();
}
