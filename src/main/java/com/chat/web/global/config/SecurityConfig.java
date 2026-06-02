package com.chat.web.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * WEB 레이어 Spring Security 설정.
 * 로그인/회원가입 페이지는 인증 없이 허용하고, 인증 흐름은 컨트롤러에서 수동으로 처리한다.
 * Spring Security의 formLogin은 사용하지 않으며, WAS API 호출 후 수동으로 SecurityContext를 설정한다.
 * SessionRegistry를 통해 동시 로그인을 1개로 제한하며, 새 로그인 시 기존 세션이 만료된다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 세션 레지스트리 빈.
     * 로그인한 사용자별 활성 세션을 추적하여 중복 로그인 감지에 사용한다.
     *
     * @return SessionRegistryImpl 인스턴스
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * HttpSession 생성/소멸 이벤트를 Spring ApplicationContext에 발행하는 리스너.
     * SessionRegistry가 세션 소멸 시 자동으로 등록 정보를 정리하려면 이 빈이 필요하다.
     *
     * @return HttpSessionEventPublisher 인스턴스
     */
    @Bean
    public static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * HTTP 보안 필터 체인 구성.
     * - /vendor/**, /css/**, /js/**, /common-ui/**, /login, /signup : 인증 없이 허용
     * - /admin/**, /api/admin/** : ROLE_ADMIN 권한 필요
     * - 그 외 모든 요청 : 인증 필수, 미인증 시 /login으로 리다이렉트
     * - 로그아웃 : 세션 초기화 후 /login으로 리다이렉트
     * - 동시 세션 : 계정당 1개 제한, 만료된 세션은 /login?expired=true로 리다이렉트
     *
     * @param http HttpSecurity 설정 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 오류 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/vendor/**", "/css/**", "/js/**", "/common-ui/**", "/login", "/signup", "/public/**").permitAll()
                // 관리자 전용 페이지 및 API는 ADMIN 권한 필요
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/login"))
                // 권한 부족(403) 시 메인 페이지로 리다이렉트
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendRedirect("/"))
            )
            .sessionManagement(session -> session
                // 계정당 최대 1개 세션 허용 (새 로그인 시 기존 세션 만료)
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/login?expired=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );
        return http.build();
    }
}
