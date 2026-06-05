package com.chat.web.common.file.controller;

import com.chat.web.auth.vo.AdminUserSessionVo;
import com.chat.web.global.common.ApiResponse;
import com.chat.web.global.common.SessionConstants;
import com.chat.web.common.file.service.FileService;
import com.chat.web.common.file.vo.FileUploadResponseVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 REST API 컨트롤러.
 * 프론트엔드의 파일 업로드/삭제/다운로드 요청을 처리하고,
 * WAS 백엔드의 파일 API를 호출한다.
 * 모든 요청은 로그인 세션을 요구한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileApiController {

    private final FileService fileService;
    private final RestClient wasRestClient;

    /**
     * 세션에서 로그인한 관리자의 adminId를 추출한다.
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
     * 파일을 업로드한다.
     * POST /api/file/upload (multipart/form-data)
     *
     * @param groupType 파일 그룹 타입 (GUIDE, RESOURCE 등)
     * @param files 업로드할 파일 목록
     * @param session HttpSession 객체
     * @return ApiResponse<FileUploadResponseVo> 업로드 결과
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponseVo>> uploadFiles(
            @RequestParam("groupType") String groupType,
            @RequestParam("files") List<MultipartFile> files,
            HttpSession session) {
        // 로그인 검증
        getAdminId(session);
        log.info("파일 업로드 요청 - groupType: {}, fileCount: {}", groupType, files.size());

        // FileService를 통해 WAS에 파일 업로드
        FileUploadResponseVo result = fileService.upload(groupType, files);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 파일을 삭제한다.
     * POST /api/file/{fileSeq}/delete
     *
     * @param fileSeq 삭제할 파일 시퀀스
     * @param session HttpSession 객체
     * @return ApiResponse<Void> 삭제 결과
     */
    @PostMapping("/{fileSeq}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable("fileSeq") Long fileSeq,
            HttpSession session) {
        // 로그인 검증
        getAdminId(session);
        log.info("파일 삭제 요청 - fileSeq: {}", fileSeq);

        // FileService를 통해 WAS에 파일 삭제
        fileService.deleteFile(fileSeq);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 파일을 다운로드한다.
     * GET /api/file/{fileSeq}/download
     * WAS에서 byte[]로 수신 후 Content-Disposition 헤더(파일명 포함)를 클라이언트에 전달한다.
     *
     * @param fileSeq 다운로드할 파일 시퀀스
     * @param session HttpSession 객체
     * @return ResponseEntity<byte[]> 파일 바이너리 및 헤더
     */
    @GetMapping("/{fileSeq}/download")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable("fileSeq") Long fileSeq,
            HttpSession session) {
        // 로그인 검증
        getAdminId(session);
        log.info("파일 다운로드 요청 - fileSeq: {}", fileSeq);

        // RestClient는 InputStream을 직접 변환할 수 없으므로 byte[]로 수신
        ResponseEntity<byte[]> wasResponse = wasRestClient.get()
                .uri("/api/v1/file/{fileSeq}/download", fileSeq)
                .retrieve()
                .toEntity(byte[].class);

        // WAS가 인코딩한 파일명(Content-Disposition)을 클라이언트에 그대로 전달
        String contentDisposition = wasResponse.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition != null ? contentDisposition : "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(wasResponse.getBody());
    }
}
