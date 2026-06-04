package com.chat.web.global.exception;

import com.chat.web.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * WEB 레이어 전역 예외 처리기.
 * RuntimeException이 컨트롤러까지 전파될 경우 HTML 500 대신
 * ApiResponse JSON 형식으로 반환하여 프론트엔드가 정상적으로 처리할 수 있게 한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 정적 리소스 미존재(404) 처리.
     * axios.min.js.map 등 소스맵 파일 요청처럼 브라우저가 자동으로 보내는
     * 부가 요청이 GlobalExceptionHandler에 도달해 ERROR 로그를 남기는 것을 방지한다.
     *
     * @param e 정적 리소스 없음 예외
     * @return 404 Not Found (body 없음)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException e) {
        log.debug("정적 리소스 없음 (무시): {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    /**
     * 서비스 레이어에서 전파된 RuntimeException 처리.
     * WAS 연결 실패, WAS 오류 응답 등 모든 RuntimeException을 JSON으로 응답한다.
     *
     * @param e 발생한 런타임 예외
     * @return 500 상태 코드와 ApiResponse.error 형식의 JSON
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("처리되지 않은 런타임 오류: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error(e.getMessage() != null ? e.getMessage() : "서버 오류가 발생했습니다."));
    }

    /**
     * 예상치 못한 일반 예외 처리.
     *
     * @param e 발생한 예외
     * @return 500 상태 코드와 ApiResponse.error 형식의 JSON
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상치 못한 서버 오류: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}
