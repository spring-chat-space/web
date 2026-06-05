package com.chat.web.guide.vo;

import com.chat.web.common.file.vo.FileVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 가이드 상세 정보 VO.
 * 가이드의 전체 내용, 파일, 좋아요 정보를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class GuideDetailVo {
    private Long guideSeq;
    private Long userSeq;
    private String authorName;
    private Long fileGroupSeq;
    private String title;
    private String content;
    private String isPublic;
    private Integer likeCount;
    private boolean myLiked;
    @JsonProperty("isMine")
    private boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileVo> files;
}
