package com.chat.web.guide.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.SessionConstants;
import com.chat.web.guide.service.GuideService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 가이드 페이지 라우팅 컨트롤러.
 * Thymeleaf 템플릿 렌더링을 담당하며, 세션에서 user 정보를 추출하여 모델에 추가한다.
 */
@Slf4j
@Controller
@RequestMapping("/guide")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    /**
     * 세션에서 로그인한 사용자 정보를 추출한다.
     *
     * @param session HttpSession 객체
     * @return 로그인한 관리자 세션 정보
     */
    private AdminUserSessionVo getUser(HttpSession session) {
        return (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
    }

    /**
     * 가이드 목록 페이지.
     * GET /guide
     *
     * @param model   Thymeleaf 모델
     * @param session HttpSession 객체
     * @return templates/guide/list.html
     */
    @GetMapping
    public String listPage(Model model, HttpSession session) {
        model.addAttribute("user", getUser(session));
        log.info("가이드 목록 페이지 접근");
        return "guide/list";
    }

    /**
     * 가이드 작성 페이지.
     * GET /guide/write
     *
     * @param model   Thymeleaf 모델
     * @param session HttpSession 객체
     * @return templates/guide/write.html
     */
    @GetMapping("/write")
    public String writePage(Model model, HttpSession session) {
        model.addAttribute("user", getUser(session));
        log.info("가이드 작성 페이지 접근");
        return "guide/write";
    }

    /**
     * 가이드 상세 페이지.
     * GET /guide/{guideSeq}
     *
     * @param guideSeq 가이드 시퀀스
     * @param model    Thymeleaf 모델
     * @param session  HttpSession 객체
     * @return templates/guide/detail.html
     */
    @GetMapping("/{guideSeq}")
    public String detailPage(@PathVariable("guideSeq") Long guideSeq, Model model, HttpSession session) {
        AdminUserSessionVo user = getUser(session);
        model.addAttribute("user", user);
        model.addAttribute("guideSeq", guideSeq);
        log.info("가이드 상세 페이지 접근 - guideSeq: {}", guideSeq);
        return "guide/detail";
    }

    /**
     * 가이드 수정 페이지.
     * GET /guide/{guideSeq}/edit
     *
     * @param guideSeq 가이드 시퀀스
     * @param model    Thymeleaf 모델
     * @param session  HttpSession 객체
     * @return templates/guide/edit.html
     */
    @GetMapping("/{guideSeq}/edit")
    public String editPage(@PathVariable("guideSeq") Long guideSeq, Model model, HttpSession session) {
        AdminUserSessionVo user = getUser(session);
        model.addAttribute("user", user);
        model.addAttribute("guideSeq", guideSeq);
        log.info("가이드 수정 페이지 접근 - guideSeq: {}", guideSeq);
        return "guide/edit";
    }
}
