package com.chat.web.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지 전송 요청 VO (WEB 레이어).
 * WAS의 ChatSendRequestVo와 동일한 구조로 JSON 직렬화에 사용된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSendRequestVo {

    /** 관리자 아이디 (세션에서 주입) */
    private String adminId;

    /** 기존 채팅방 ID (신규 채팅방이면 null) */
    private Long roomId;

    /** 사용자가 입력한 메시지 내용 */
    private String content;
}
