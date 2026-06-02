package com.chat.web.chat.service;

import com.chat.web.chat.vo.ChatMessageVo;
import com.chat.web.chat.vo.ChatRoomVo;
import com.chat.web.chat.vo.ChatSendRequestVo;
import com.chat.web.chat.vo.ChatSendResponseVo;

import java.util.List;

/**
 * WEB 레이어 채팅 서비스 인터페이스.
 * WAS 채팅 API를 프록시하는 메서드를 정의한다.
 */
public interface ChatService {

    /**
     * 채팅방 목록 조회.
     *
     * @param adminId 조회할 관리자 아이디
     * @return 최근 수정 순 채팅방 목록
     */
    List<ChatRoomVo> getRooms(String adminId);

    /**
     * 채팅방 검색.
     *
     * @param adminId 관리자 아이디
     * @param keyword 검색 키워드
     * @return 검색된 채팅방 목록
     */
    List<ChatRoomVo> searchRooms(String adminId, String keyword);

    /**
     * 메시지 전송 및 AI 응답 수신.
     *
     * @param request 메시지 전송 요청 (adminId, roomId, content)
     * @return AI 응답, roomId, 신규 채팅방 제목
     */
    ChatSendResponseVo sendMessage(ChatSendRequestVo request);

    /**
     * 채팅방 메시지 히스토리 조회.
     *
     * @param roomId  조회할 채팅방 ID
     * @param adminId 소유자 확인용 관리자 아이디
     * @return 시간순 메시지 목록
     */
    List<ChatMessageVo> getMessages(Long roomId, String adminId);

    /**
     * 채팅방 삭제 (논리적 삭제).
     *
     * @param roomId  삭제할 채팅방 ID
     * @param adminId 소유자 확인용 관리자 아이디
     */
    void deleteRoom(Long roomId, String adminId);
}
