package com.chat.web.common.file.service.impl;

import com.chat.web.global.common.WasApiResponse;
import com.chat.web.common.file.service.FileService;
import com.chat.web.common.file.vo.FileUploadResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 서비스 구현.
 * WAS 백엔드의 파일 API를 RestClient로 호출하여 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final RestClient wasRestClient;

    /**
     * 파일을 업로드한다.
     * 선택된 파일들을 multipart/form-data 형식으로 WAS에 전송한다.
     *
     * @param groupType 파일 그룹 타입 (GUIDE 등)
     * @param files 업로드할 파일 목록
     * @return 업로드 결과 VO (fileGroupSeq, 파일 목록 포함)
     * @throws RuntimeException WAS API 호출 실패 시
     */
    @Override
    public FileUploadResponseVo upload(String groupType, List<MultipartFile> files) {
        log.info("WAS 파일 업로드 - groupType: {}, count: {}", groupType, files.size());
        try {
            // multipart/form-data 바디 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("groupType", groupType);

            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
                ByteArrayResource resource = new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                };
                body.add("files", resource);
            }

            // WAS 업로드 API 호출
            WasApiResponse<FileUploadResponseVo> response = wasRestClient.post()
                    .uri("/api/v1/file/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<FileUploadResponseVo>>() {});

            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            // WAS API 에러 응답 처리
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "파일 업로드 중 오류가 발생했습니다.";
            log.error("WAS 파일 업로드 오류: {}", msg);
            throw new RuntimeException(msg);
        } catch (Exception e) {
            log.error("파일 업로드 서비스 오류: {}", e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 파일을 삭제한다.
     * 지정된 파일 시퀀스를 기반으로 WAS에 삭제 요청을 전송한다.
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @throws RuntimeException WAS API 호출 실패 시
     */
    @Override
    public void deleteFile(Long fileSeq) {
        log.info("WAS 파일 삭제 - fileSeq: {}", fileSeq);
        try {
            // WAS 삭제 API 호출
            wasRestClient.post()
                    .uri("/api/v1/file/{fileSeq}/delete", fileSeq)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            // WAS API 에러 응답 처리
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "파일 삭제 중 오류가 발생했습니다.";
            log.error("WAS 파일 삭제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }
}
