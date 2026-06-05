package com.chat.web.common.file.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 파일 업로드 응답 VO.
 * 업로드된 파일들과 파일 그룹 시퀀스를 담는다.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileUploadResponseVo {
    private Long fileGroupSeq;
    private List<FileVo> files;
}
