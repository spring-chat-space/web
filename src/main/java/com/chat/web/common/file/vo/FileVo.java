package com.chat.web.common.file.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 파일 정보 VO.
 * 업로드된 파일의 메타데이터를 담는다.
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
