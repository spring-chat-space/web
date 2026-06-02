package com.chat.web.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 VO (WEB 레이어).
 * 프론트엔드에서 전달받아 WAS API로 그대로 전달한다.
 */
@Getter
@NoArgsConstructor
public class AdminUserUpdateRequestVo {

    /** 수정할 이름 */
    private String adminName;

    /** 수정할 이메일 */
    private String email;

    /** 수정할 전화번호 */
    private String phoneNumber;

    /** 수정할 권한 (ROLE_USER / ROLE_ADMIN) */
    private String role;

    /** 계정 활성화 여부 (Y / N) */
    private String useYn;
}
