package com.chat.web.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 목록 응답 VO (WEB 레이어).
 * WAS의 ChatRoomVo와 동일한 구조로 RestClient JSON 역직렬화에 사용된다.
 */
@Getter
@NoArgsConstructor
public class ChatRoomVo {

    /** 채팅방 ID */
    private Long roomId;

    /** 채팅방 제목 */
    private String title;

    /** 마지막 수정 일시 */
    private LocalDateTime updatedAt;

    /** 생성 일시 */
    private LocalDateTime createdAt;
}
