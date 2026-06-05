package com.chat.web.guide.service;

import com.chat.web.guide.vo.*;
import java.util.List;

/**
 * 가이드 관리 서비스 인터페이스.
 * WAS 가이드 API(/api/v1/guide)를 프록시한다.
 */
public interface GuideService {
    /**
     * 가이드 목록 조회 (정렬, 검색 기능 포함).
     * GET /api/v1/guide
     *
     * @param adminId  로그인한 사용자 ID (좋아요 여부 판단용)
     * @param sort     정렬 기준 (like, recent 등)
     * @param keyword  검색 키워드 (선택사항)
     * @return 가이드 목록
     */
    List<GuideListItemVo> getGuideList(String adminId, String sort, String keyword);

    /**
     * 가이드 상세 조회.
     * GET /api/v1/guide/{guideSeq}
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  로그인한 사용자 ID (좋아요 여부, 작성자 여부 판단용)
     * @return 가이드 상세 정보 (파일 목록 포함)
     * @throws RuntimeException 가이드를 찾을 수 없을 때
     */
    GuideDetailVo getGuideDetail(Long guideSeq, String adminId);

    /**
     * 가이드 등록.
     * POST /api/v1/guide
     *
     * @param request 가이드 저장 요청 정보
     * @return 등록된 가이드의 시퀀스
     * @throws RuntimeException 등록 실패 시
     */
    Long createGuide(GuideSaveRequestVo request);

    /**
     * 가이드 수정.
     * POST /api/v1/guide/{guideSeq}/update
     *
     * @param guideSeq 가이드 시퀀스
     * @param request  수정할 가이드 정보
     * @throws RuntimeException 수정 실패 시
     */
    void updateGuide(Long guideSeq, GuideSaveRequestVo request);

    /**
     * 가이드 삭제.
     * POST /api/v1/guide/{guideSeq}/delete
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  요청 사용자 ID (권한 검증용)
     * @throws RuntimeException 삭제 실패 시
     */
    void deleteGuide(Long guideSeq, String adminId);

    /**
     * 가이드 좋아요 토글.
     * POST /api/v1/guide/{guideSeq}/like
     *
     * @param guideSeq 가이드 시퀀스
     * @param adminId  요청 사용자 ID
     * @return 변경된 좋아요 수와 현재 사용자의 좋아요 여부
     * @throws RuntimeException 토글 실패 시
     */
    GuideLikeResponseVo toggleLike(Long guideSeq, String adminId);
}
