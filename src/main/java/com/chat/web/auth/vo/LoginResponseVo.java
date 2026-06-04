package com.chat.web.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 성공 응답 데이터 전송 객체 (WEB 레이어).
 * WAS의 LoginResponseVo와 동일한 구조로, RestClient JSON 역직렬화에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginResponseVo {

    /** 관리자 아이디 */
    private String adminId;

    /** 관리자 이름 */
    private String adminName;

    /** 복호화된 이메일 */
    private String email;

    /** 사용자 권한 (ROLE_USER, ROLE_ADMIN) */
    private String role;
}
