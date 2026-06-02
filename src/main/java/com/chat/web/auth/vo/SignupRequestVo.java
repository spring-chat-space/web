package com.chat.web.auth.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 데이터 전송 객체 (WEB 레이어).
 * 회원가입 폼 데이터를 수집하여 WAS API로 전달한다.
 */
@Getter
@Setter
public class SignupRequestVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 평문 비밀번호 */
    private String password;

    /** 관리자 이름 */
    private String adminName;

    /** 이메일 */
    private String email;

    /** 전화번호 */
    private String phoneNumber;
}
