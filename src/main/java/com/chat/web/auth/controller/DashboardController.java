package com.chat.web.auth.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.SessionConstants;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 대시보드 화면 라우팅 컨트롤러 (WEB 레이어).
 * 인증된 사용자만 접근 가능하며 (SecurityConfig에서 보장), Gemini 스타일 대시보드를 렌더링한다.
 */
@Slf4j
@Controller
public class DashboardController {

    /**
     * 메인 대시보드 페이지 렌더링.
     * 세션에서 사용자 정보를 꺼내 템플릿 모델에 전달한다.
     *
     * @param model   템플릿 모델
     * @param session HTTP 세션
     * @return 메인 대시보드 Thymeleaf 템플릿 경로
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        AdminUserSessionVo user =
                (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
        model.addAttribute("user", user);
        log.info("대시보드 접근 - ID: {}", user != null ? user.getAdminId() : "unknown");
        return "main/index";
    }
}
