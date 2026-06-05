package com.chat.web.guide.service;

import com.chat.web.guide.vo.CommentListResponseVo;
import java.util.Map;

/**
 * 댓글 관리 서비스 인터페이스.
 * WAS 댓글 API(/api/v1/comment)를 프록시한다.
 */
public interface CommentService {
    /**
     * 댓글 목록 조회 (도메인별, 참조ID별).
     * GET /api/v1/comment
     *
     * @param domainType 도메인 타입 (GUIDE 등)
     * @param refId      참조 ID (가이드 시퀀스 등)
     * @param adminId    로그인한 사용자 ID (작성자 여부 판단용)
     * @return 댓글 목록 (트리 구조, 대댓글 포함)
     */
    CommentListResponseVo getComments(String domainType, Long refId, String adminId);

    /**
     * 댓글 등록.
     * POST /api/v1/comment
     *
     * @param payload 댓글 저장 요청 정보 (domainType, refId, adminId, content, parentSeq 포함)
     * @return 등록된 댓글의 시퀀스
     * @throws RuntimeException 등록 실패 시
     */
    Long createComment(Map<String, Object> payload);

    /**
     * 댓글 수정.
     * POST /api/v1/comment/{commentSeq}/update
     *
     * @param commentSeq 댓글 시퀀스
     * @param payload    수정할 댓글 정보 (adminId, content 포함)
     * @throws RuntimeException 수정 실패 시
     */
    void updateComment(Long commentSeq, Map<String, Object> payload);

    /**
     * 댓글 삭제.
     * POST /api/v1/comment/{commentSeq}/delete
     *
     * @param commentSeq 댓글 시퀀스
     * @param adminId    요청 사용자 ID (권한 검증용)
     * @throws RuntimeException 삭제 실패 시
     */
    void deleteComment(Long commentSeq, String adminId);
}
