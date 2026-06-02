package com.chat.web.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 VO (WEB 레이어).
 * WAS의 ChatMessageVo와 동일한 구조로 RestClient JSON 역직렬화에 사용된다.
 */
@Getter
@NoArgsConstructor
public class ChatMessageVo {

    /** 메시지 고유 ID */
    private Long messageId;

    /** 소속 채팅방 ID */
    private Long roomId;

    /** 송신자 타입 ('USER' 또는 'AI') */
    private String senderType;

    /** 메시지 내용 */
    private String content;

    /** 메시지 생성 일시 */
    private LocalDateTime createdAt;
}
