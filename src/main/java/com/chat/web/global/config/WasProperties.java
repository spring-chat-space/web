package com.chat.web.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WAS 연동 설정 프로퍼티.
 * application.yml의 'was.*' 프로퍼티를 타입 안전하게 바인딩한다.
 * spring-boot-configuration-processor가 메타데이터를 생성하여 IDE 자동완성/검증을 지원.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "was")
public class WasProperties {

    /** WAS API 서버 기본 URL (예: http://localhost:8081) */
    private String baseUrl;
}
