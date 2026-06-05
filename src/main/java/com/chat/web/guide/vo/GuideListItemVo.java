package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 가이드 목록 항목 VO.
 * 가이드 목록 조회 시 각 가이드의 기본 정보를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class GuideListItemVo {
    private Long guideSeq;
    private Long userSeq;
    private String title;
    private String isPublic;
    private Integer likeCount;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean myLiked;
}
