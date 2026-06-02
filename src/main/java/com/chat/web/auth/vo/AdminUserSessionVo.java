package com.chat.web.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 로그인 성공 후 HttpSession 및 Spring Security 컨텍스트에 저장되는 사용자 세션 정보 객체.
 * HttpSession에 저장되므로 Serializable을 구현해야 한다.
 */
@Getter
@AllArgsConstructor
public class AdminUserSessionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 로그인한 관리자 아이디 */
    private String adminId;

    /** 로그인한 관리자 이름 */
    private String adminName;

    /** 복호화된 이메일 */
    private String email;

    /** 사용자 권한 (ROLE_USER, ROLE_ADMIN) */
    private String role;
}
