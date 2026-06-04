package com.chat.web.ocr.service.impl;

import com.chat.web.global.common.WasApiResponse;
import com.chat.web.ocr.service.OcrService;
import com.chat.web.ocr.vo.OcrResultVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

/**
 * WEB 레이어 OCR 서비스 구현체.
 * MultipartFile을 ByteArrayResource로 변환하여 WAS OCR API에 multipart/form-data로 프록시한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final RestClient wasRestClient;

    /**
     * {@inheritDoc}
     * MultipartFile을 ByteArrayResource로 변환하여 WAS POST /api/v1/ocr/receipt에 전달한다.
     * WAS 연결 실패 또는 에러 응답 시 RuntimeException으로 전파한다.
     *
     * @param image 영수증 이미지 파일
     * @return OCR 분석 결과 VO
     */
    @Override
    public OcrResultVo analyzeReceipt(MultipartFile image) {
        log.info("WAS OCR 분석 요청 - 파일명: {}, 크기: {} bytes",
                image.getOriginalFilename(), image.getSize());
        try {
            // MultipartFile을 ByteArrayResource로 변환 (파일명 유지)
            byte[] bytes = image.getBytes();
            String filename = image.getOriginalFilename() != null
                    ? image.getOriginalFilename() : "receipt.jpg";
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", resource);

            WasApiResponse<OcrResultVo> response = wasRestClient.post()
                    .uri("/api/v1/ocr/receipt")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<OcrResultVo>>() {});

            return response != null ? response.getResult() : null;

        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (OCR 분석): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                    new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage() : "영수증 분석 중 오류가 발생했습니다.";
            log.error("WAS OCR 분석 오류: {}", msg);
            throw new RuntimeException(msg);
        } catch (Exception e) {
            log.error("OCR 서비스 오류: {}", e.getMessage());
            throw new RuntimeException("영수증 분석 중 오류가 발생했습니다.");
        }
    }
}
