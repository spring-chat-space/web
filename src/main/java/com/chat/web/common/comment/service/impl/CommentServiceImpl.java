package com.chat.web.common.comment.service.impl;

import com.chat.web.common.comment.service.CommentService;
import com.chat.web.common.comment.vo.CommentListResponseVo;
import com.chat.web.global.common.WasApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;

/**
 * WEB 레이어 댓글 서비스 구현체.
 * WAS 댓글 API를 프록시한다.
 * 여러 도메인에서 공통으로 재사용 가능.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final RestClient wasRestClient;

    /**
     * {@inheritDoc}
     * GET /api/v1/comment?domainType=&refId=&adminId=
     */
    @Override
    public CommentListResponseVo getComments(String domainType, Long refId, String adminId) {
        log.info("WAS 댓글 목록 조회 - domainType: {}, refId: {}, adminId: {}", domainType, refId, adminId);
        try {
            WasApiResponse<CommentListResponseVo> response = wasRestClient.get()
                    .uri(b -> b.path("/api/v1/comment")
                            .queryParam("domainType", domainType)
                            .queryParam("refId", refId)
                            .queryParam("adminId", adminId)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<CommentListResponseVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            log.error("WAS 댓글 목록 조회 오류: {}", e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/comment
     */
    @Override
    public Long createComment(Map<String, Object> payload) {
        log.info("WAS 댓글 등록");
        try {
            WasApiResponse<Long> response = wasRestClient.post()
                    .uri("/api/v1/comment")
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Long>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "댓글 등록 중 오류가 발생했습니다.";
            log.error("WAS 댓글 등록 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/comment/{commentSeq}/update
     */
    @Override
    public void updateComment(Long commentSeq, Map<String, Object> payload) {
        log.info("WAS 댓글 수정 - commentSeq: {}", commentSeq);
        try {
            wasRestClient.post()
                    .uri("/api/v1/comment/{commentSeq}/update", commentSeq)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "댓글 수정 중 오류가 발생했습니다.";
            log.error("WAS 댓글 수정 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/comment/{commentSeq}/delete?adminId=
     */
    @Override
    public void deleteComment(Long commentSeq, String adminId) {
        log.info("WAS 댓글 삭제 - commentSeq: {}", commentSeq);
        try {
            wasRestClient.post()
                    .uri(b -> b.path("/api/v1/comment/{commentSeq}/delete")
                            .queryParam("adminId", adminId)
                            .build(commentSeq))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "댓글 삭제 중 오류가 발생했습니다.";
            log.error("WAS 댓글 삭제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }
}
