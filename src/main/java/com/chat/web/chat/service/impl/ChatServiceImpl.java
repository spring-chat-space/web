package com.chat.web.chat.service.impl;

import com.chat.web.chat.service.ChatService;
import com.chat.web.chat.vo.ChatMessageVo;
import com.chat.web.chat.vo.ChatRoomVo;
import com.chat.web.chat.vo.ChatSendRequestVo;
import com.chat.web.chat.vo.ChatSendResponseVo;
import com.chat.web.global.common.WasApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * WEB 레이어 채팅 서비스 구현체.
 * RestClient를 통해 WAS 채팅 API를 프록시하고, 에러 응답 발생 시 메시지를 RuntimeException으로 전파한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RestClient wasRestClient;

    /**
     * {@inheritDoc}
     * GET /api/v1/chat/rooms?adminId=
     */
    @Override
    public List<ChatRoomVo> getRooms(String adminId) {
        log.info("WAS 채팅방 목록 조회 - adminId: {}", adminId);
        try {
            WasApiResponse<List<ChatRoomVo>> response = wasRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/chat/rooms")
                            .queryParam("adminId", adminId)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<List<ChatRoomVo>>>() {});
            return response != null && response.getResult() != null ? response.getResult() : List.of();
        } catch (RestClientResponseException e) {
            log.error("WAS 채팅방 목록 조회 오류: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     * GET /api/v1/chat/rooms/search?adminId=&q=
     */
    @Override
    public List<ChatRoomVo> searchRooms(String adminId, String keyword) {
        log.info("WAS 채팅방 검색 - adminId: {}, keyword: {}", adminId, keyword);
        try {
            WasApiResponse<List<ChatRoomVo>> response = wasRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/chat/rooms/search")
                            .queryParam("adminId", adminId)
                            .queryParam("q", keyword)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<List<ChatRoomVo>>>() {});
            return response != null && response.getResult() != null ? response.getResult() : List.of();
        } catch (RestClientResponseException e) {
            log.error("WAS 채팅방 검색 오류: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/chat/send
     */
    @Override
    public ChatSendResponseVo sendMessage(ChatSendRequestVo request) {
        log.info("WAS 메시지 전송 - adminId: {}, roomId: {}", request.getAdminId(), request.getRoomId());
        try {
            WasApiResponse<ChatSendResponseVo> response = wasRestClient.post()
                    .uri("/api/v1/chat/send")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<ChatSendResponseVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                    new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String errorMessage = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage() : "메시지 전송 중 오류가 발생했습니다.";
            log.error("WAS 메시지 전송 오류 응답: {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * {@inheritDoc}
     * GET /api/v1/chat/rooms/{roomId}/messages?adminId=
     */
    @Override
    public List<ChatMessageVo> getMessages(Long roomId, String adminId) {
        log.info("WAS 채팅 메시지 조회 - roomId: {}, adminId: {}", roomId, adminId);
        try {
            WasApiResponse<List<ChatMessageVo>> response = wasRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/chat/rooms/{roomId}/messages")
                            .queryParam("adminId", adminId)
                            .build(roomId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<List<ChatMessageVo>>>() {});
            return response != null && response.getResult() != null ? response.getResult() : List.of();
        } catch (RestClientResponseException e) {
            log.error("WAS 채팅 메시지 조회 오류: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/chat/rooms/{roomId}/delete?adminId=
     */
    @Override
    public void deleteRoom(Long roomId, String adminId) {
        log.info("WAS 채팅방 삭제 - roomId: {}, adminId: {}", roomId, adminId);
        try {
            wasRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/chat/rooms/{roomId}/delete")
                            .queryParam("adminId", adminId)
                            .build(roomId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> errorResponse = e.getResponseBodyAs(
                    new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String errorMessage = (errorResponse != null && errorResponse.getMessage() != null)
                    ? errorResponse.getMessage() : "채팅방 삭제 중 오류가 발생했습니다.";
            log.error("WAS 채팅방 삭제 오류 응답: {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
