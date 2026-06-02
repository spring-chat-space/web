package com.chat.web.admin.service.impl;

import com.chat.web.admin.service.AdminService;
import com.chat.web.admin.vo.AdminUserCreateRequestVo;
import com.chat.web.admin.vo.AdminUserDetailVo;
import com.chat.web.admin.vo.AdminUserListResponseVo;
import com.chat.web.admin.vo.AdminUserUpdateRequestVo;
import com.chat.web.global.common.WasApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * WEB 레이어 관리자 서비스 구현체.
 * RestClient를 통해 WAS admin API를 프록시한다.
 * 에러 응답 발생 시 WAS의 메시지를 RuntimeException으로 전파한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RestClient wasRestClient;

    /**
     * WAS 에러 응답에서 메시지를 추출하는 공통 헬퍼.
     *
     * @param e            RestClient 에러
     * @param defaultMsg   WAS 메시지가 없을 때 사용할 기본 메시지
     * @return 에러 메시지 문자열
     */
    private String extractErrorMessage(RestClientResponseException e, String defaultMsg) {
        WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                new ParameterizedTypeReference<WasApiResponse<Object>>() {});
        return (errorResponse != null && errorResponse.getMessage() != null)
                ? errorResponse.getMessage() : defaultMsg;
    }

    /**
     * {@inheritDoc}
     * GET /api/v1/admin/users?keyword=&role=&status=&page=&size=
     */
    @Override
    public AdminUserListResponseVo getUsers(String keyword, String role, String status, int page, int size) {
        log.info("WAS 사용자 목록 조회 - keyword: {}, role: {}, status: {}, page: {}, size: {}",
                keyword, role, status, page, size);
        try {
            WasApiResponse<AdminUserListResponseVo> response = wasRestClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/api/v1/admin/users")
                                .queryParam("page", page)
                                .queryParam("size", size);
                        // null 또는 빈 값은 쿼리 파라미터에서 제외
                        if (keyword != null && !keyword.isBlank()) builder.queryParam("keyword", keyword);
                        if (role    != null && !role.isBlank())    builder.queryParam("role", role);
                        if (status  != null && !status.isBlank())  builder.queryParam("status", status);
                        return builder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<AdminUserListResponseVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (사용자 목록 조회): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "사용자 목록 조회 중 오류가 발생했습니다.");
            log.error("WAS 사용자 목록 조회 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * GET /api/v1/admin/users/{adminId}
     */
    @Override
    public AdminUserDetailVo getUser(String adminId) {
        log.info("WAS 사용자 상세 조회 - adminId: {}", adminId);
        try {
            WasApiResponse<AdminUserDetailVo> response = wasRestClient.get()
                    .uri("/api/v1/admin/users/{adminId}", adminId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<AdminUserDetailVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (사용자 상세 조회): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "사용자 조회 중 오류가 발생했습니다.");
            log.error("WAS 사용자 상세 조회 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/admin/users/{adminId}/update
     */
    @Override
    public void updateUser(String adminId, AdminUserUpdateRequestVo request) {
        log.info("WAS 사용자 정보 수정 - adminId: {}", adminId);
        try {
            wasRestClient.post()
                    .uri("/api/v1/admin/users/{adminId}/update", adminId)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (사용자 정보 수정): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "사용자 정보 수정 중 오류가 발생했습니다.");
            log.error("WAS 사용자 정보 수정 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/admin/users/{adminId}/unlock
     */
    @Override
    public void unlockUser(String adminId) {
        log.info("WAS 계정 잠금 해제 - adminId: {}", adminId);
        try {
            wasRestClient.post()
                    .uri("/api/v1/admin/users/{adminId}/unlock", adminId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (계정 잠금 해제): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "계정 잠금 해제 중 오류가 발생했습니다.");
            log.error("WAS 계정 잠금 해제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/admin/users/{adminId}/reset-password
     */
    @Override
    public Map<String, String> resetPassword(String adminId) {
        log.info("WAS 비밀번호 초기화 - adminId: {}", adminId);
        try {
            WasApiResponse<Map<String, String>> response = wasRestClient.post()
                    .uri("/api/v1/admin/users/{adminId}/reset-password", adminId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Map<String, String>>>() {});
            return response != null && response.getResult() != null ? response.getResult() : Map.of();
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (비밀번호 초기화): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "비밀번호 초기화 중 오류가 발생했습니다.");
            log.error("WAS 비밀번호 초기화 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/admin/users/{adminId}/delete
     */
    @Override
    public void deleteUser(String adminId) {
        log.info("WAS 사용자 논리 삭제 - adminId: {}", adminId);
        try {
            wasRestClient.post()
                    .uri("/api/v1/admin/users/{adminId}/delete", adminId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (사용자 삭제): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "사용자 삭제 중 오류가 발생했습니다.");
            log.error("WAS 사용자 삭제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/admin/users/create
     */
    @Override
    public void createUser(AdminUserCreateRequestVo request) {
        log.info("WAS 신규 사용자 생성 - adminId: {}", request.getAdminId());
        try {
            wasRestClient.post()
                    .uri("/api/v1/admin/users/create")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (ResourceAccessException e) {
            log.error("WAS 연결 실패 (신규 사용자 생성): {}", e.getMessage());
            throw new RuntimeException("WAS 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        } catch (RestClientResponseException e) {
            String msg = extractErrorMessage(e, "신규 사용자 생성 중 오류가 발생했습니다.");
            log.error("WAS 신규 사용자 생성 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }
}
