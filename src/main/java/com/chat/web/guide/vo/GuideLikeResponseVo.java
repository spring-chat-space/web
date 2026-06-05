package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가이드 좋아요 응답 VO.
 * 좋아요 토글 후 변경된 좋아요 수와 현재 사용자의 좋아요 여부를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class GuideLikeResponseVo {
    private Integer likeCount;
    private boolean liked;
}
