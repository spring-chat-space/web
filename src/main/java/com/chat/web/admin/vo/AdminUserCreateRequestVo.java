package com.chat.web.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * WEB 레이어 사용자 신규 생성 요청 VO.
 * WAS 레이어의 AdminUserCreateRequestVo를 프록시하는 DTO.
 * Validation은 WAS 레이어에서 수행하므로 이 VO는 검증 애노테이션을 포함하지 않음.
 */
@Getter
@NoArgsConstructor
public class AdminUserCreateRequestVo {

    /** 생성할 관리자 아이디 */
    private String adminId;

    /** 비밀번호 */
    private String password;

    /** 관리자 이름 */
    private String adminName;

    /** 이메일 */
    private String email;

    /** 전화번호 (선택) */
    private String phoneNumber;

    /** 사용자 권한 (선택, 기본값: ROLE_USER) */
    private String role;
}
