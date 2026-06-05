package com.chat.web.common.comment.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 댓글 목록 응답 VO.
 * 댓글 목록 조회 결과를 담는다.
 * 여러 도메인에서 공통으로 재사용 가능.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentListResponseVo {
    private int totalCount;
    private List<CommentVo> comments;
}
