package com.chat.web.admin.controller;

import com.chat.web.admin.service.AdminService;
import com.chat.web.admin.vo.AdminUserCreateRequestVo;
import com.chat.web.admin.vo.AdminUserDetailVo;
import com.chat.web.admin.vo.AdminUserListResponseVo;
import com.chat.web.admin.vo.AdminUserUpdateRequestVo;
import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.ApiResponse;
import com.chat.web.global.common.SessionConstants;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WEB 레이어 사용자 관리 컨트롤러.
 * 페이지 렌더링(GET /admin/users)과 REST API 프록시(GET|POST /api/admin/users/**)를 담당한다.
 * SecurityConfig에서 /admin/** 및 /api/admin/** 경로에 ROLE_ADMIN 권한을 요구한다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 사용자 관리 페이지 렌더링.
     * GET /admin/users
     *
     * @param model   템플릿 모델 (sidebar용 user 정보 포함)
     * @param session HTTP 세션
     * @return 사용자 관리 Thymeleaf 템플릿 경로
     */
    @GetMapping("/admin/users")
    public String usersPage(Model model, HttpSession session) {
        AdminUserSessionVo user =
                (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
        model.addAttribute("user", user);
        log.info("사용자 관리 페이지 접근 - adminId: {}", user != null ? user.getAdminId() : "unknown");
        return "admin/users";
    }

    /**
     * 사용자 목록 조회 API.
     * GET /api/admin/users?keyword=&role=&status=&page=0&size=10
     *
     * @param keyword 검색 키워드 (선택)
     * @param role    권한 필터 (선택)
     * @param status  상태 필터 (선택)
     * @param page    페이지 번호 (0부터, 기본값 0)
     * @param size    페이지당 건수 (기본값 10)
     * @return 페이징 처리된 사용자 목록
     */
    @GetMapping("/api/admin/users")
    @ResponseBody
    public ResponseEntity<ApiResponse<AdminUserListResponseVo>> getUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "role",    required = false) String role,
            @RequestParam(name = "status",  required = false) String status,
            @RequestParam(name = "page",    defaultValue = "0")  int page,
            @RequestParam(name = "size",    defaultValue = "10") int size) {
        log.info("사용자 목록 조회 - keyword: {}, role: {}, status: {}, page: {}, size: {}",
                keyword, role, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getUsers(keyword, role, status, page, size)));
    }

    /**
     * 사용자 단건 상세 조회 API.
     * GET /api/admin/users/{adminId}
     *
     * @param adminId 조회할 사용자 아이디
     * @return 사용자 상세 정보
     */
    @GetMapping("/api/admin/users/{adminId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<AdminUserDetailVo>> getUser(
            @PathVariable("adminId") String adminId) {
        log.info("사용자 상세 조회 - adminId: {}", adminId);
        return ResponseEntity.ok(ApiResponse.success(adminService.getUser(adminId)));
    }

    /**
     * 사용자 정보 수정 API.
     * POST /api/admin/users/{adminId}/update
     *
     * @param adminId 수정할 사용자 아이디
     * @param request 수정 요청 데이터
     * @return 성공 시 200 OK
     */
    @PostMapping("/api/admin/users/{adminId}/update")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable("adminId") String adminId,
            @RequestBody AdminUserUpdateRequestVo request) {
        log.info("사용자 정보 수정 - adminId: {}", adminId);
        adminService.updateUser(adminId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 계정 잠금 해제 API.
     * POST /api/admin/users/{adminId}/unlock
     *
     * @param adminId 잠금 해제할 사용자 아이디
     * @return 성공 시 200 OK
     */
    @PostMapping("/api/admin/users/{adminId}/unlock")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> unlockUser(
            @PathVariable("adminId") String adminId) {
        log.info("계정 잠금 해제 - adminId: {}", adminId);
        adminService.unlockUser(adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 비밀번호 초기화 API.
     * POST /api/admin/users/{adminId}/reset-password
     *
     * @param adminId 비밀번호를 초기화할 사용자 아이디
     * @return 임시 비밀번호를 담은 Map {"tempPassword": "..."}
     */
    @PostMapping("/api/admin/users/{adminId}/reset-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @PathVariable("adminId") String adminId) {
        log.info("비밀번호 초기화 - adminId: {}", adminId);
        return ResponseEntity.ok(ApiResponse.success(adminService.resetPassword(adminId)));
    }

    /**
     * 사용자 논리 삭제 API.
     * POST /api/admin/users/{adminId}/delete
     *
     * @param adminId 삭제할 사용자 아이디
     * @return 성공 시 200 OK
     */
    @PostMapping("/api/admin/users/{adminId}/delete")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable("adminId") String adminId) {
        log.info("사용자 삭제 - adminId: {}", adminId);
        adminService.deleteUser(adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 신규 사용자 계정 생성 API.
     * POST /api/admin/users/create
     *
     * @param request 생성 요청 데이터 (adminId, password, adminName, email, phoneNumber, role)
     * @return 성공 시 200 OK
     */
    @PostMapping("/api/admin/users/create")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> createUser(
            @RequestBody AdminUserCreateRequestVo request) {
        log.info("신규 사용자 생성 - adminId: {}", request.getAdminId());
        adminService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
