package com.chat.web.auth.service.impl;

import com.chat.web.auth.service.AuthService;
import com.chat.web.auth.vo.LoginRequestVo;
import com.chat.web.auth.vo.LoginResponseVo;
import com.chat.web.auth.vo.SignupRequestVo;
import com.chat.web.global.common.WasApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * WEB 레이어 인증 서비스 구현체.
 * RestClient를 통해 WAS API를 호출하고, 에러 응답 발생 시 메시지를 추출하여 RuntimeException으로 전파한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RestClient wasRestClient;

    /**
     * WAS 회원가입 API 호출.
     * POST /api/v1/auth/signup
     *
     * @param signupRequestVo 회원가입 요청 데이터
     * @throws RuntimeException WAS에서 400/500 에러 응답 시 에러 메시지를 담아 전파
     */
    @Override
    public void signup(SignupRequestVo signupRequestVo) {
        log.info("WAS 회원가입 API 호출 - ID: {}", signupRequestVo.getAdminId());
        try {
            wasRestClient.post()
                    .uri("/api/v1/auth/signup")
                    .body(signupRequestVo)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            // WAS가 반환한 에러 응답 바디에서 메시지를 추출하여 예외로 전파
            WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                    new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String errorMessage = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage() : "회원가입 처리 중 오류가 발생했습니다.";
            log.error("WAS 회원가입 API 오류 응답: {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * WAS 로그인 API 호출.
     * POST /api/v1/auth/login
     *
     * @param loginRequestVo 로그인 요청 데이터
     * @return 로그인 성공 시 사용자 정보 (LoginResponseVo)
     * @throws RuntimeException WAS에서 400/500 에러 응답 시 에러 메시지를 담아 전파
     */
    @Override
    public LoginResponseVo login(LoginRequestVo loginRequestVo) {
        log.info("WAS 로그인 API 호출 - ID: {}", loginRequestVo.getAdminId());
        try {
            WasApiResponse<LoginResponseVo> response = wasRestClient.post()
                    .uri("/api/v1/auth/login")
                    .body(loginRequestVo)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<LoginResponseVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                    new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String errorMessage = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage() : "로그인 처리 중 오류가 발생했습니다.";
            log.error("WAS 로그인 API 오류 응답: {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
