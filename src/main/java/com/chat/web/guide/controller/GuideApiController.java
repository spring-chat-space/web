package com.chat.web.guide.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.ApiResponse;
import com.chat.web.global.common.SessionConstants;
import com.chat.web.global.common.WasApiResponse;
import com.chat.web.guide.service.CommentService;
import com.chat.web.guide.service.FileService;
import com.chat.web.guide.service.GuideService;
import com.chat.web.guide.vo.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 가이드 REST API 프록시 컨트롤러 (WEB).
 * 프론트엔드 JavaScript 요청을 받아 WAS 백엔드 API를 호출하고,
 * 세션에서 adminId를 추출하여 요청에 포함시킨다.
 * 모든 응답은 ApiResponse 래퍼로 반환된다.
 */
@Slf4j
@RestController
@RequestMapping("/api/guide")
@RequiredArgsConstructor
public class GuideApiController {

    private final GuideService guideService;
    private final FileService fileService;
    private final CommentService commentService;
    private final RestClient wasRestClient;

    /**
     * 세션에서 로그인한 사용자의 adminId를 추출한다.
     * 로그인하지 않은 상태에서는 IllegalStateException을 발생시킨다.
     *
     * @param session HttpSession 객체
     * @return 로그인한 관리자 ID
     * @throws IllegalStateException 로그인하지 않은 경우
     */
    private String getAdminId(HttpSession session) {
        AdminUserSessionVo vo = (AdminUserSessionVo) session.getAttribute(SessionConstants.ADMIN_USER_SESSION);
        if (vo == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return vo.getAdminId();
    }

    /**
     * 가이드 목록 조회.
     * GET /api/guide/list?sort=like&keyword=검색어
     *
     * @param sort     정렬 기준 (기본값: like)
     * @param keyword  검색 키워드 (선택사항)
     * @param session  HttpSession 객체
     * @return ApiResponse<List<GuideListItemVo>>
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<GuideListItemVo>>> getList(
            @RequestParam(value = "sort", defaultValue = "like") String sort,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session) {
        String adminId = getAdminId(session);
        return ResponseEntity.ok(ApiResponse.success(guideService.getGuideList(adminId, sort, keyword)));
    }

    /**
     * 가이드 상세 조회.
     * GET /api/guide/{guideSeq}
     *
     * @param guideSeq 가이드 시퀀스
     * @param session  HttpSession 객체
     * @return ApiResponse<GuideDetailVo>
     */
    @GetMapping("/{guideSeq}")
    public ResponseEntity<ApiResponse<GuideDetailVo>> getDetail(
            @PathVariable("guideSeq") Long guideSeq, HttpSession session) {
        String adminId = getAdminId(session);
        return ResponseEntity.ok(ApiResponse.success(guideService.getGuideDetail(guideSeq, adminId)));
    }

    /**
     * 가이드 등록.
     * POST /api/guide/create
     * 요청 본문: { title, content, isPublic?, fileGroupSeq? }
     *
     * @param body    가이드 정보 JSON
     * @param session HttpSession 객체
     * @return ApiResponse<Long> (등록된 가이드 시퀀스)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createGuide(
            @RequestBody Map<String, Object> body, HttpSession session) {
        String adminId = getAdminId(session);
        GuideSaveRequestVo request = new GuideSaveRequestVo();
        request.setAdminId(adminId);
        request.setTitle((String) body.get("title"));
        request.setContent((String) body.get("content"));
        request.setIsPublic(body.get("isPublic") != null ? (String) body.get("isPublic") : "Y");
        if (body.get("fileGroupSeq") != null) {
            request.setFileGroupSeq(Long.valueOf(body.get("fileGroupSeq").toString()));
        }
        return ResponseEntity.ok(ApiResponse.success(guideService.createGuide(request)));
    }

    /**
     * 가이드 수정.
     * POST /api/guide/{guideSeq}/update
     *
     * @param guideSeq 가이드 시퀀스
     * @param body     수정할 가이드 정보 JSON
     * @param session  HttpSession 객체
     * @return ApiResponse<Void>
     */
    @PostMapping("/{guideSeq}/update")
    public ResponseEntity<ApiResponse<Void>> updateGuide(
            @PathVariable("guideSeq") Long guideSeq,
            @RequestBody Map<String, Object> body, HttpSession session) {
        String adminId = getAdminId(session);
        GuideSaveRequestVo request = new GuideSaveRequestVo();
        request.setAdminId(adminId);
        request.setTitle((String) body.get("title"));
        request.setContent((String) body.get("content"));
        request.setIsPublic(body.get("isPublic") != null ? (String) body.get("isPublic") : "Y");
        if (body.get("fileGroupSeq") != null) {
            request.setFileGroupSeq(Long.valueOf(body.get("fileGroupSeq").toString()));
        }
        guideService.updateGuide(guideSeq, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 가이드 삭제.
     * POST /api/guide/{guideSeq}/delete
     *
     * @param guideSeq 가이드 시퀀스
     * @param session  HttpSession 객체
     * @return ApiResponse<Void>
     */
    @PostMapping("/{guideSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteGuide(
            @PathVariable("guideSeq") Long guideSeq, HttpSession session) {
        String adminId = getAdminId(session);
        guideService.deleteGuide(guideSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 가이드 좋아요 토글.
     * POST /api/guide/{guideSeq}/like
     *
     * @param guideSeq 가이드 시퀀스
     * @param session  HttpSession 객체
     * @return ApiResponse<GuideLikeResponseVo>
     */
    @PostMapping("/{guideSeq}/like")
    public ResponseEntity<ApiResponse<GuideLikeResponseVo>> toggleLike(
            @PathVariable("guideSeq") Long guideSeq, HttpSession session) {
        String adminId = getAdminId(session);
        return ResponseEntity.ok(ApiResponse.success(guideService.toggleLike(guideSeq, adminId)));
    }

    /**
     * 파일 업로드.
     * POST /api/guide/file/upload (multipart/form-data)
     *
     * @param groupType 파일 그룹 타입 (GUIDE 등)
     * @param files     업로드할 파일 목록
     * @param session   HttpSession 객체
     * @return ApiResponse<FileUploadResponseVo>
     */
    @PostMapping("/file/upload")
    public ResponseEntity<ApiResponse<FileUploadResponseVo>> uploadFiles(
            @RequestParam("groupType") String groupType,
            @RequestParam("files") List<MultipartFile> files,
            HttpSession session) {
        getAdminId(session);
        return ResponseEntity.ok(ApiResponse.success(fileService.upload(groupType, files)));
    }

    /**
     * 파일 삭제.
     * POST /api/guide/file/{fileSeq}/delete
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @param session HttpSession 객체
     * @return ApiResponse<Void>
     */
    @PostMapping("/file/{fileSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable("fileSeq") Long fileSeq, HttpSession session) {
        getAdminId(session);
        fileService.deleteFile(fileSeq);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 파일 다운로드.
     * GET /api/guide/file/{fileSeq}/download
     * WAS에서 직접 InputStream 스트림으로 받아 클라이언트에 전달한다.
     *
     * @param fileSeq 다운로드할 파일 시퀀스
     * @param session HttpSession 객체
     * @return InputStreamResource (파일 바이너리 스트림)
     * @throws RuntimeException 파일을 찾을 수 없는 경우
     */
    @GetMapping("/file/{fileSeq}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable("fileSeq") Long fileSeq, HttpSession session) {
        getAdminId(session);

        // WAS에서 파일 바이너리 스트림을 직접 받아온다
        InputStream is = wasRestClient.get()
                .uri("/api/v1/file/{fileSeq}/download", fileSeq)
                .retrieve()
                .body(InputStream.class);

        if (is == null) {
            throw new RuntimeException("파일을 찾을 수 없습니다.");
        }

        // 파일 첨부 응답으로 반환 (다운로드 트리거)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(is));
    }

    /**
     * 댓글 목록 조회.
     * GET /api/guide/{guideSeq}/comments
     *
     * @param guideSeq 가이드 시퀀스
     * @param session  HttpSession 객체
     * @return ApiResponse<CommentListResponseVo>
     */
    @GetMapping("/{guideSeq}/comments")
    public ResponseEntity<ApiResponse<CommentListResponseVo>> getComments(
            @PathVariable("guideSeq") Long guideSeq, HttpSession session) {
        String adminId = getAdminId(session);
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments("GUIDE", guideSeq, adminId)));
    }

    /**
     * 댓글 등록.
     * POST /api/guide/{guideSeq}/comments
     * 요청 본문: { content, parentSeq? }
     * adminId, domainType, refId는 자동으로 추가된다.
     *
     * @param guideSeq 가이드 시퀀스
     * @param body     댓글 정보 JSON
     * @param session  HttpSession 객체
     * @return ApiResponse<Long> (등록된 댓글 시퀀스)
     */
    @PostMapping("/{guideSeq}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(
            @PathVariable("guideSeq") Long guideSeq,
            @RequestBody Map<String, Object> body, HttpSession session) {
        String adminId = getAdminId(session);
        Map<String, Object> payload = new HashMap<>(body);
        payload.put("adminId", adminId);
        payload.put("domainType", "GUIDE");
        payload.put("refId", guideSeq);
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(payload)));
    }

    /**
     * 댓글 수정.
     * POST /api/guide/comments/{commentSeq}/update
     *
     * @param commentSeq 댓글 시퀀스
     * @param body       수정할 댓글 정보 JSON
     * @param session    HttpSession 객체
     * @return ApiResponse<Void>
     */
    @PostMapping("/comments/{commentSeq}/update")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable("commentSeq") Long commentSeq,
            @RequestBody Map<String, Object> body, HttpSession session) {
        String adminId = getAdminId(session);
        Map<String, Object> payload = new HashMap<>(body);
        payload.put("adminId", adminId);
        commentService.updateComment(commentSeq, payload);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 댓글 삭제.
     * POST /api/guide/comments/{commentSeq}/delete
     *
     * @param commentSeq 댓글 시퀀스
     * @param session    HttpSession 객체
     * @return ApiResponse<Void>
     */
    @PostMapping("/comments/{commentSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentSeq") Long commentSeq, HttpSession session) {
        String adminId = getAdminId(session);
        commentService.deleteComment(commentSeq, adminId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
