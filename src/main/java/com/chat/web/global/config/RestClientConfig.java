package com.chat.web.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * WAS API 통신용 RestClient 설정.
 * Spring 6.x의 RestClient를 사용하여 WAS 레이어와 HTTP 통신을 처리한다.
 * WasProperties를 통해 타입 안전하게 was.base-url을 주입받는다.
 */
@Configuration
@EnableConfigurationProperties(WasProperties.class)
public class RestClientConfig {

    /**
     * WAS 통신용 RestClient Bean 등록.
     * baseUrl과 기본 Content-Type(application/json)을 설정한다.
     *
     * @param wasProperties WAS 연동 설정 프로퍼티
     * @return 설정된 RestClient 인스턴스
     */
    @Bean
    public RestClient wasRestClient(WasProperties wasProperties) {
        return RestClient.builder()
                .baseUrl(wasProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
