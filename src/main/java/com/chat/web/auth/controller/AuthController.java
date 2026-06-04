package com.chat.web.auth.controller;

import com.chat.web.auth.service.AuthService;
import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.auth.vo.LoginRequestVo;
import com.chat.web.auth.vo.LoginResponseVo;
import com.chat.web.auth.vo.SignupRequestVo;
import com.chat.web.global.common.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 인증 화면 라우팅 컨트롤러 (WEB 레이어).
 * 로그인/회원가입 페이지 렌더링 및 폼 데이터 처리를 담당한다.
 * Spring Security의 formLogin을 사용하지 않고, WAS API 호출 후 수동으로 SecurityContext를 설정한다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionRegistry sessionRegistry;

    /**
     * 로그인 페이지 렌더링.
     * 이미 인증된 사용자는 메인 대시보드로 리다이렉트한다.
     *
     * @param model   템플릿 모델
     * @param session HTTP 세션
     * @return 로그인 Thymeleaf 템플릿 경로
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute(SessionConstants.ADMIN_USER_SESSION) != null) {
            return "redirect:/";
        }
        model.addAttribute("loginRequestVo", new LoginRequestVo());
        return "auth/login";
    }

    /**
     * 로그인 폼 처리.
     * WAS 로그인 API 호출 후 성공 시 Spring Security 컨텍스트와 세션에 사용자 정보를 저장한다.
     *
     * @param loginRequestVo     로그인 폼 데이터
     * @param redirectAttributes 리다이렉트 시 전달할 메시지
     * @param request            HttpServletRequest (Security Context 세션 저장용)
     * @return 성공 시 메인 대시보드로 리다이렉트, 실패 시 로그인 페이지 재표시
     */
    @PostMapping("/login")
    public String loginProcess(@ModelAttribute LoginRequestVo loginRequestVo,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            LoginResponseVo loginResponseVo = authService.login(loginRequestVo);

            AdminUserSessionVo sessionVo = new AdminUserSessionVo(
                    loginResponseVo.getAdminId(),
                    loginResponseVo.getAdminName(),
                    loginResponseVo.getEmail(),
                    loginResponseVo.getRole()
            );

            // Spring Security 컨텍스트에 인증 정보 수동 설정 (WAS에서 받은 실제 권한 사용)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(sessionVo, null,
                            List.of(new SimpleGrantedAuthority(loginResponseVo.getRole())));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 동일 계정의 기존 활성 세션을 모두 만료 처리 (중복 로그인 방지)
            // ConcurrentSessionFilter가 다음 요청에서 만료된 세션을 감지해 로그인 페이지로 튕긴다
            sessionRegistry.getAllSessions(loginResponseVo.getAdminId(), false)
                    .forEach(SessionInformation::expireNow);

            // Security Context를 HttpSession에 저장하여 다음 요청에서도 인증 상태 유지
            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            session.setAttribute(SessionConstants.ADMIN_USER_SESSION, sessionVo);

            // 새 세션을 SessionRegistry에 등록 (adminId를 principal로 사용)
            sessionRegistry.registerNewSession(session.getId(), loginResponseVo.getAdminId());

            log.info("로그인 성공 - ID: {}", loginResponseVo.getAdminId());
            return "redirect:/";

        } catch (RuntimeException e) {
            log.error("로그인 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * 회원가입 페이지 렌더링.
     *
     * @param model 템플릿 모델
     * @return 회원가입 Thymeleaf 템플릿 경로
     */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequestVo", new SignupRequestVo());
        return "auth/signup";
    }

    /**
     * 회원가입 폼 처리.
     * WAS 회원가입 API 호출 후 성공 시 로그인 페이지로 리다이렉트한다.
     * 실패 시 redirect 대신 뷰를 직접 반환하여 입력값을 유지한다.
     * (@ModelAttribute 파라미터는 자동으로 Model에 바인딩되므로 폼 입력값이 그대로 유지됨)
     *
     * @param signupRequestVo    회원가입 폼 데이터 (자동으로 model에 "signupRequestVo"로 등록)
     * @param model              에러 메시지 전달용 모델
     * @param redirectAttributes 성공 시 리다이렉트 메시지 전달용
     * @return 성공 시 로그인 페이지로 리다이렉트, 실패 시 회원가입 뷰 직접 반환
     */
    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute SignupRequestVo signupRequestVo,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            authService.signup(signupRequestVo);
            log.info("회원가입 성공 - ID: {}", signupRequestVo.getAdminId());
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            // redirect 대신 뷰 직접 반환: 입력값이 model에 남아 폼에 재표시됨
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/signup";
        }
    }
}
