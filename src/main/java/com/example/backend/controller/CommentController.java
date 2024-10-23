package com.example.backend.controller;

import com.example.backend.common.response.CommonApiResponse;
import com.example.backend.common.response.SliceApiResponse;
import com.example.backend.dto.request.project.CommentRequestDto;
import com.example.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/comments/{projectId}")
    public CommonApiResponse<?> create(@PathVariable Long projectId, @RequestBody String content, Authentication authentication) {
        return new CommonApiResponse<>(OK, commentService.create(projectId, content, authentication));
    }

    // 댓글 목록 가져오기
    @GetMapping("/comments/{projectId}")
    public SliceApiResponse<?> findList(@PathVariable Long projectId, @RequestParam int page, @RequestParam int size) {
        return commentService.findList(projectId, page, size);
    }

    // 댓글 수정
    @PatchMapping("/comments/{projectId}")
    public CommonApiResponse<?> update(@PathVariable Long projectId, @RequestBody CommentRequestDto request, Authentication authentication) {
        return new CommonApiResponse<>(OK, commentService.update(projectId, request, authentication));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{projectId}")
    public CommonApiResponse<?> delete(@PathVariable Long projectId, @RequestParam Long commentId, Authentication authentication) {
        return new CommonApiResponse<>(OK, commentService.delete(projectId, commentId, authentication));
    }

}
