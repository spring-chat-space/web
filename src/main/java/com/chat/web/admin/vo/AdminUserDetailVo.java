package com.chat.web.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 상세 조회 응답 VO (WEB 레이어).
 * WAS API 응답 result를 역직렬화하는 객체.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminUserDetailVo {

    private String adminId;
    private String adminName;
    private String email;
    private String phoneNumber;
    private String role;
    private String useYn;
    private int loginFailCount;
    private LocalDateTime lockedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** WAS에서 계산된 상태 값 (active | inactive | locked) */
    private String status;
}
