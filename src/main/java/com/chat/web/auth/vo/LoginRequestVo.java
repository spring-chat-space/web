package com.chat.web.auth.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 데이터 전송 객체 (WEB 레이어).
 * 로그인 폼 데이터를 수집하여 WAS API로 전달한다.
 */
@Getter
@Setter
public class LoginRequestVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 평문 비밀번호 */
    private String password;
}
