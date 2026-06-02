package com.chat.web.global.common;

/**
 * HttpSession 키 상수 정의 클래스.
 * 세션에 저장되는 속성 키를 중앙 관리하여 오타로 인한 버그를 방지한다.
 */
public final class SessionConstants {

    /** 로그인한 관리자 세션 정보 키 */
    public static final String ADMIN_USER_SESSION = "ADMIN_USER_SESSION";

    private SessionConstants() {}
}
