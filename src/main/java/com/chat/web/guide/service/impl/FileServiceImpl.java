package com.chat.web.guide.service.impl;

import com.chat.web.global.common.WasApiResponse;
import com.chat.web.guide.service.FileService;
import com.chat.web.guide.vo.FileUploadResponseVo;
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
 * WEB 레이어 파일 서비스 구현체.
 * MultipartFile을 ByteArrayResource로 변환하여 WAS 파일 업로드 API에 프록시한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final RestClient wasRestClient;

    /**
     * {@inheritDoc}
     * POST /api/v1/file/upload (multipart)
     */
    @Override
    public FileUploadResponseVo upload(String groupType, List<MultipartFile> files) {
        log.info("WAS 파일 업로드 - groupType: {}, count: {}", groupType, files.size());
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("groupType", groupType);

            // MultipartFile 객체를 ByteArrayResource로 변환하여 body에 추가
            for (MultipartFile file : files) {
                byte[] bytes = file.getBytes();
                String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
                ByteArrayResource resource = new ByteArrayResource(bytes) {
                    @Override public String getFilename() { return filename; }
                };
                body.add("files", resource);
            }

            // WAS POST 요청
            WasApiResponse<FileUploadResponseVo> response = wasRestClient.post()
                    .uri("/api/v1/file/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<FileUploadResponseVo>>() {});

            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
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
     * {@inheritDoc}
     * POST /api/v1/file/{fileSeq}/delete
     */
    @Override
    public void deleteFile(Long fileSeq) {
        log.info("WAS 파일 삭제 - fileSeq: {}", fileSeq);
        try {
            wasRestClient.post()
                    .uri("/api/v1/file/{fileSeq}/delete", fileSeq)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "파일 삭제 중 오류가 발생했습니다.";
            log.error("WAS 파일 삭제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }
}
