package com.chat.web.ocr.service;

import com.chat.web.ocr.vo.OcrResultVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 영수증 OCR 서비스 인터페이스 (WEB 레이어).
 * WAS OCR API에 이미지를 프록시하여 분석 결과를 반환한다.
 */
public interface OcrService {

    /**
     * 영수증 이미지를 WAS로 전달하여 OCR 분석 결과를 반환한다.
     *
     * @param image 영수증 이미지 파일
     * @return OCR 분석 결과
     */
    OcrResultVo analyzeReceipt(MultipartFile image);
}
