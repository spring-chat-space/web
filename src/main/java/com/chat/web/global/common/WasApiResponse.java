package com.chat.web.global.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * WAS API 응답 역직렬화용 공통 래퍼 클래스.
 * WAS 레이어의 ApiResponse&lt;T&gt;와 동일한 구조로, RestClient가 응답 JSON을 이 객체로 변환한다.
 *
 * @param <T> 실제 응답 데이터 타입
 */
@Getter
@Setter
@NoArgsConstructor
public class WasApiResponse<T> {

    /** 성공 여부 */
    private boolean success;

    /** 결과 메시지 */
    private String message;

    /** 실제 반환 데이터 */
    private T result;
}
