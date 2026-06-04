package com.chat.web.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 목록 단건 VO (WEB 레이어).
 * WAS API 응답 result.items 배열의 각 항목을 역직렬화하는 객체.
 */
@Getter
@Setter
@NoArgsConstructor
public class AdminUserListItemVo {

    private String adminId;
    private String adminName;
    private String email;
    private String phoneNumber;
    private String role;
    private String useYn;
    private LocalDateTime lockedUntil;
    private LocalDateTime createdAt;

    /** WAS에서 계산된 상태 값 (active | inactive | locked) */
    private String status;
}
