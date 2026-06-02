package com.chat.web.admin.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 목록 페이징 응답 VO (WEB 레이어).
 * WAS API 응답 result를 역직렬화하는 객체.
 */
@Getter
@NoArgsConstructor
public class AdminUserListResponseVo {

    /** 현재 페이지 사용자 목록 */
    private List<AdminUserListItemVo> items;

    /** 전체 조회 건수 */
    private long totalCount;

    /** 전체 페이지 수 */
    private int totalPages;

    /** 현재 페이지 번호 (0부터 시작) */
    private int currentPage;

    /** 페이지당 건수 */
    private int size;
}
