package com.chat.web.guide.service;

import com.chat.web.guide.vo.FileUploadResponseVo;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 가이드 파일 관리 서비스 인터페이스.
 * WAS 파일 API(/api/v1/file)를 프록시한다.
 */
public interface FileService {
    /**
     * 파일 업로드 (WAS /api/v1/file/upload 프록시).
     * MultipartFile 목록을 ByteArrayResource로 변환하여 전송한다.
     *
     * @param groupType 도메인 구분 (GUIDE 등)
     * @param files     업로드할 파일 목록
     * @return WAS로부터 받은 fileGroupSeq와 파일 목록
     * @throws RuntimeException WAS API 호출 실패 시
     */
    FileUploadResponseVo upload(String groupType, List<MultipartFile> files);

    /**
     * 파일 단건 삭제 (WAS /api/v1/file/{fileSeq}/delete 프록시).
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @throws RuntimeException WAS API 호출 실패 시
     */
    void deleteFile(Long fileSeq);
}
