package com.chat.web.common.file.service;

import com.chat.web.common.file.vo.FileUploadResponseVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 서비스 인터페이스.
 * WAS 백엔드 API를 통해 파일 업로드/삭제를 처리한다.
 */
public interface FileService {

    /**
     * 파일을 업로드한다.
     * WAS의 /api/v1/file/upload 엔드포인트를 호출한다.
     *
     * @param groupType 파일 그룹 타입 (GUIDE, RESOURCE 등)
     * @param files 업로드할 파일 목록
     * @return 업로드 결과 (fileGroupSeq, 파일 목록)
     * @throws RuntimeException 업로드 실패 시
     */
    FileUploadResponseVo upload(String groupType, List<MultipartFile> files);

    /**
     * 파일을 삭제한다.
     * WAS의 /api/v1/file/{fileSeq}/delete 엔드포인트를 호출한다.
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @throws RuntimeException 삭제 실패 시
     */
    void deleteFile(Long fileSeq);
}
