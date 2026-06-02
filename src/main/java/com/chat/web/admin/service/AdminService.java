package com.chat.web.admin.service;

import com.chat.web.admin.vo.*;

import java.util.Map;

/**
 * 사용자 관리 서비스 인터페이스 (WEB 레이어).
 * WAS admin API를 RestClient로 호출하는 프록시 역할.
 */
public interface AdminService {

    /**
     * 사용자 목록 조회 (조건 + 페이징).
     *
     * @param keyword  검색 키워드 (선택)
     * @param role     권한 필터 (선택)
     * @param status   상태 필터 (선택)
     * @param page     페이지 번호 (0부터)
     * @param size     페이지당 건수
     * @return 페이징 처리된 사용자 목록
     */
    AdminUserListResponseVo getUsers(String keyword, String role, String status, int page, int size);

    /**
     * 사용자 단건 상세 조회.
     *
     * @param adminId 조회할 사용자 아이디
     * @return 사용자 상세 정보
     */
    AdminUserDetailVo getUser(String adminId);

    /**
     * 사용자 정보 수정.
     *
     * @param adminId 수정할 사용자 아이디
     * @param request 수정 요청 데이터
     */
    void updateUser(String adminId, AdminUserUpdateRequestVo request);

    /**
     * 계정 잠금 해제.
     *
     * @param adminId 잠금 해제할 사용자 아이디
     */
    void unlockUser(String adminId);

    /**
     * 비밀번호 초기화.
     *
     * @param adminId 비밀번호를 초기화할 사용자 아이디
     * @return 임시 비밀번호를 담은 Map {"tempPassword": "..."}
     */
    Map<String, String> resetPassword(String adminId);

    /**
     * 사용자 논리 삭제.
     *
     * @param adminId 삭제할 사용자 아이디
     */
    void deleteUser(String adminId);

    /**
     * 신규 사용자 계정 생성.
     *
     * @param request 생성 요청 데이터 (adminId, password, adminName, email, phoneNumber, role)
     */
    void createUser(AdminUserCreateRequestVo request);
}
