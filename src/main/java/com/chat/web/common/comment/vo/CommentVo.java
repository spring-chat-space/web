package com.chat.web.common.comment.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 정보 VO.
 * 댓글 및 대댓글 트리 구조를 담는다.
 * 여러 도메인에서 공통으로 재사용 가능.
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
    @JsonProperty("isMine")
    private boolean isMine;
    private List<CommentVo> replies = new ArrayList<>();
}
