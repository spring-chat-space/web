package com.chat.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * chat-web 애플리케이션 진입점.
 * Thymeleaf 화면 라우팅 및 WAS REST API 통신 담당 레이어.
 */
@SpringBootApplication
public class ChatWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatWebApplication.class, args);
    }
}
