package com.chat.web.auth.service;

import com.chat.web.auth.vo.LoginRequestVo;
import com.chat.web.auth.vo.LoginResponseVo;
import com.chat.web.auth.vo.SignupRequestVo;

/**
 * WEB 레이어 인증 서비스 인터페이스.
 * WAS API를 호출하여 회원가입 및 로그인을 처리한다.
 */
public interface AuthService {

    /**
     * WAS API를 통한 회원가입 처리.
     *
     * @param signupRequestVo 회원가입 요청 데이터
     * @throws RuntimeException WAS API 호출 실패 또는 비즈니스 오류 발생 시
     */
    void signup(SignupRequestVo signupRequestVo);

    /**
     * WAS API를 통한 로그인 처리.
     *
     * @param loginRequestVo 로그인 요청 데이터
     * @return 로그인 성공 시 사용자 정보 (LoginResponseVo)
     * @throws RuntimeException WAS API 호출 실패 또는 인증 오류 발생 시
     */
    LoginResponseVo login(LoginRequestVo loginRequestVo);
}
