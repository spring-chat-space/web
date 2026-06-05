package com.chat.web.guide.service.impl;

import com.chat.web.global.common.WasApiResponse;
import com.chat.web.guide.service.GuideService;
import com.chat.web.guide.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.List;

/**
 * WEB 레이어 가이드 서비스 구현체.
 * WAS 가이드 API를 프록시하며, 에러 응답 발생 시 RuntimeException으로 전파한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final RestClient wasRestClient;

    /**
     * {@inheritDoc}
     * GET /api/v1/guide?adminId=&sort=&keyword=
     */
    @Override
    public List<GuideListItemVo> getGuideList(String adminId, String sort, String keyword) {
        log.info("WAS 가이드 목록 조회 - adminId: {}, sort: {}, keyword: {}", adminId, sort, keyword);
        try {
            WasApiResponse<List<GuideListItemVo>> response = wasRestClient.get()
                    .uri(b -> b.path("/api/v1/guide")
                            .queryParam("adminId", adminId)
                            .queryParam("sort", sort != null ? sort : "like")
                            .queryParamIfPresent("keyword", java.util.Optional.ofNullable(keyword))
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<List<GuideListItemVo>>>() {});
            return response != null && response.getResult() != null ? response.getResult() : List.of();
        } catch (RestClientResponseException e) {
            log.error("WAS 가이드 목록 조회 오류: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     * GET /api/v1/guide/{guideSeq}?adminId=
     */
    @Override
    public GuideDetailVo getGuideDetail(Long guideSeq, String adminId) {
        log.info("WAS 가이드 상세 조회 - guideSeq: {}, adminId: {}", guideSeq, adminId);
        try {
            WasApiResponse<GuideDetailVo> response = wasRestClient.get()
                    .uri(b -> b.path("/api/v1/guide/{guideSeq}")
                            .queryParam("adminId", adminId)
                            .build(guideSeq))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<GuideDetailVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "가이드를 찾을 수 없습니다.";
            log.error("WAS 가이드 상세 조회 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/guide
     */
    @Override
    public Long createGuide(GuideSaveRequestVo request) {
        log.info("WAS 가이드 등록 - adminId: {}", request.getAdminId());
        try {
            WasApiResponse<Long> response = wasRestClient.post()
                    .uri("/api/v1/guide")
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Long>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "가이드 등록 중 오류가 발생했습니다.";
            log.error("WAS 가이드 등록 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/guide/{guideSeq}/update
     */
    @Override
    public void updateGuide(Long guideSeq, GuideSaveRequestVo request) {
        log.info("WAS 가이드 수정 - guideSeq: {}", guideSeq);
        try {
            wasRestClient.post()
                    .uri("/api/v1/guide/{guideSeq}/update", guideSeq)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "가이드 수정 중 오류가 발생했습니다.";
            log.error("WAS 가이드 수정 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/guide/{guideSeq}/delete?adminId=
     */
    @Override
    public void deleteGuide(Long guideSeq, String adminId) {
        log.info("WAS 가이드 삭제 - guideSeq: {}", guideSeq);
        try {
            wasRestClient.post()
                    .uri(b -> b.path("/api/v1/guide/{guideSeq}/delete")
                            .queryParam("adminId", adminId)
                            .build(guideSeq))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<Void>>() {});
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "가이드 삭제 중 오류가 발생했습니다.";
            log.error("WAS 가이드 삭제 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * {@inheritDoc}
     * POST /api/v1/guide/{guideSeq}/like?adminId=
     */
    @Override
    public GuideLikeResponseVo toggleLike(Long guideSeq, String adminId) {
        log.info("WAS 좋아요 토글 - guideSeq: {}, adminId: {}", guideSeq, adminId);
        try {
            WasApiResponse<GuideLikeResponseVo> response = wasRestClient.post()
                    .uri(b -> b.path("/api/v1/guide/{guideSeq}/like")
                            .queryParam("adminId", adminId)
                            .build(guideSeq))
                    .retrieve()
                    .body(new ParameterizedTypeReference<WasApiResponse<GuideLikeResponseVo>>() {});
            return response != null ? response.getResult() : null;
        } catch (RestClientResponseException e) {
            WasApiResponse<?> err = e.getResponseBodyAs(new ParameterizedTypeReference<WasApiResponse<Object>>() {});
            String msg = (err != null && err.getMessage() != null) ? err.getMessage() : "좋아요 처리 중 오류가 발생했습니다.";
            log.error("WAS 좋아요 토글 오류: {}", msg);
            throw new RuntimeException(msg);
        }
    }
}
