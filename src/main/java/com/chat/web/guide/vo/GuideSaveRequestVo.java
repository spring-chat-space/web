package com.chat.web.guide.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가이드 저장 요청 VO.
 * 가이드 등록 및 수정 시 사용하는 요청 객체.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuideSaveRequestVo {
    private String adminId;
    private String title;
    private String content;
    private String isPublic = "Y";
    private Long fileGroupSeq;
}
