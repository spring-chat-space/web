package com.chat.web.chat.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.chat.service.ChatService;
import com.chat.web.chat.vo.ChatMessageVo;
import com.chat.web.chat.vo.ChatRoomVo;
import com.chat.web.chat.vo.ChatSendRequestVo;
import com.chat.web.chat.vo.ChatSendResponseVo;
import com.chat.web.global.common.ApiResponse;
import com.chat.web.global.common.SessionConstants;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * WEB 레이어 채팅 REST API 컨트롤러.
 * 세션에서 adminId를 추출하여 WAS 채팅 API를 프록시한다.
 * 모든 응답은 ApiResponse&lt;T&gt; 포맷으로 반환하며, 프론트엔드는 data.result로 실제 데이터에 접근한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 세션에서 로그인한 관리자 아이디를 추출하는 헬퍼 메서드.
     *
     * @param session HTTP 세션
     * @return 로그인한 관리자 아이디
     * @throws IllegalStateException 세션에 사용자 정보가 없는 경우
     */
    private String getAdminId(HttpSession session) {
        AdminUserSessionVo sessionVo = (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
        if (sessionVo == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return sessionVo.getAdminId();
    }

    /**
     * 채팅방 목록 조회.
     * GET /api/chat/rooms
     *
     * @param session HTTP 세션 (adminId 추출용)
     * @return 최근 수정 순 채팅방 목록
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomVo>>> getRooms(HttpSession session) {
        String adminId = getAdminId(session);
        log.info("채팅방 목록 조회 - adminId: {}", adminId);
        return ResponseEntity.ok(ApiResponse.success(chatService.getRooms(adminId)));
    }

    /**
     * 채팅방 검색.
     * GET /api/chat/rooms/search?q=keyword
     *
     * @param q       검색 키워드
     * @param session HTTP 세션 (adminId 추출용)
     * @return 검색된 채팅방 목록
     */
    @GetMapping("/rooms/search")
    public ResponseEntity<ApiResponse<List<ChatRoomVo>>> searchRooms(
            @RequestParam(name = "q", required = false) String q,
            HttpSession session) {
        String adminId = getAdminId(session);
        log.info("채팅방 검색 - adminId: {}, keyword: {}", adminId, q);
        return ResponseEntity.ok(ApiResponse.success(chatService.searchRooms(adminId, q)));
    }

    /**
     * 메시지 전송 및 AI 응답 수신.
     * POST /api/chat/send
     * 요청 바디: { roomId: Long|null, content: String }
     * 세션에서 adminId를 주입하므로 클라이언트는 adminId를 전송하지 않아도 된다.
     *
     * @param body    요청 바디 (roomId, content)
     * @param session HTTP 세션 (adminId 추출용)
     * @return AI 응답, roomId, 신규 채팅방 제목
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatSendResponseVo>> sendMessage(
            @RequestBody Map<String, Object> body,
            HttpSession session) {
        String adminId = getAdminId(session);
        Long roomId = body.get("roomId") != null ? Long.valueOf(body.get("roomId").toString()) : null;
        String content = (String) body.get("content");
        log.info("메시지 전송 - adminId: {}, roomId: {}", adminId, roomId);
        return ResponseEntity.ok(ApiResponse.success(chatService.sendMessage(new ChatSendRequestVo(adminId, roomId, content))));
    }

    /**
     * 채팅방 메시지 히스토리 조회.
     * GET /api/chat/rooms/{roomId}/messages
     *
     * @param roomId  조회할 채팅방 ID
     * @param session HTTP 세션 (adminId 추출용)
     * @return 시간순 메시지 목록
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageVo>>> getMessages(
            @PathVariable("roomId") Long roomId,
            HttpSession session) {
        String adminId = getAdminId(session);
        log.info("채팅 메시지 조회 - roomId: {}, adminId: {}", roomId, adminId);
        return ResponseEntity.ok(ApiResponse.success(chatService.getMessages(roomId, adminId)));
    }

    /**
     * 채팅방 삭제 (논리적 삭제).
     * POST /api/chat/rooms/{roomId}/delete
     *
     * @param roomId  삭제할 채팅방 ID
     * @param session HTTP 세션 (adminId 추출용)
     * @return 성공 시 200 OK
     */
    @PostMapping("/rooms/{roomId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable("roomId") Long roomId, HttpSession session) {
        String adminId = getAdminId(session);
        log.info("채팅방 삭제 - roomId: {}, adminId: {}", roomId, adminId);
        chatService.deleteRoom(roomId, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
