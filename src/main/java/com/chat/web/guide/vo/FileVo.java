package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 파일 정보 VO.
 * WAS로부터 받은 파일 메타데이터를 담는 객체.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileVo {
    private Long fileSeq;
    private Long fileGroupSeq;
    private String originalName;
    private Long fileSize;
    private String fileExt;
    private LocalDateTime createdAt;
}
