package com.chat.web.ocr.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.ApiResponse;
import com.chat.web.global.common.SessionConstants;
import com.chat.web.ocr.service.OcrService;
import com.chat.web.ocr.vo.OcrResultVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * WEB 레이어 영수증 OCR 컨트롤러.
 * 페이지 렌더링(GET /ocr/receipt)과 REST API 프록시(POST /api/ocr/receipt)를 담당한다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    /**
     * 영수증 OCR 페이지 렌더링.
     * GET /ocr/receipt
     *
     * @param model   템플릿 모델 (sidebar용 user 정보 포함)
     * @param session HTTP 세션
     * @return 영수증 OCR Thymeleaf 템플릿 경로
     */
    @GetMapping("/ocr/receipt")
    public String receiptPage(Model model, HttpSession session) {
        AdminUserSessionVo user =
                (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
        model.addAttribute("user", user);
        log.info("영수증 OCR 페이지 접근 - adminId: {}", user != null ? user.getAdminId() : "unknown");
        return "ocr/receipt";
    }

    /**
     * 영수증 OCR 분석 API.
     * POST /api/ocr/receipt
     * multipart/form-data로 이미지를 수신하여 WAS OCR API로 프록시하고 결과를 반환한다.
     *
     * @param image 업로드된 영수증 이미지 파일
     * @return OCR 분석 결과 (가맹점 정보, 결제 내역, 총액)
     */
    @PostMapping("/api/ocr/receipt")
    @ResponseBody
    public ResponseEntity<ApiResponse<OcrResultVo>> analyzeReceipt(
            @RequestParam("image") MultipartFile image) {
        log.info("영수증 OCR 분석 - 파일명: {}, 크기: {} bytes",
                image.getOriginalFilename(), image.getSize());
        OcrResultVo result = ocrService.analyzeReceipt(image);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
