package com.chat.web.guide.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 파일 업로드 응답 VO.
 * WAS 파일 업로드 API가 반환하는 fileGroupSeq와 파일 목록.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileUploadResponseVo {
    private Long fileGroupSeq;
    private List<FileVo> files;
}
