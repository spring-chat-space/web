package com.chat.web.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메시지 전송 응답 VO (WEB 레이어).
 * WAS의 ChatSendResponseVo와 동일한 구조로 RestClient JSON 역직렬화에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatSendResponseVo {

    /** 채팅방 ID (신규 방 생성 시 새로 할당된 ID) */
    private Long roomId;

    /** AI 응답 텍스트 */
    private String aiContent;

    /** 신규 방 생성 시 자동 생성된 채팅방 제목 (기존 방이면 null) */
    private String roomTitle;
}
