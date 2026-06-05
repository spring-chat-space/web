package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 댓글 목록 응답 VO.
 * 가이드의 댓글 목록 조회 결과를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentListResponseVo {
    private int totalCount;
    private List<CommentVo> comments;
}
