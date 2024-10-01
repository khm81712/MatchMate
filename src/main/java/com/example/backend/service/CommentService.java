package com.example.backend.service;

import com.example.backend.common.response.SliceApiResponse;
import com.example.backend.domain.Comment;
import com.example.backend.domain.Project;
import com.example.backend.domain.User;
import com.example.backend.dto.request.project.CommentRequestDto;
import com.example.backend.dto.response.comment.CommentResponseDto;
import com.example.backend.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final JwtService jwtService;

    // 댓글 저장
    public String create(Long projectId, String content, Authentication authentication) {

        commentRepository.save(Comment.builder()
                .user(User.builder().userId(jwtService.getUserIdFromAuthentication(authentication)).build())
                .project(Project.builder().projectId(projectId).build())
                .content(content)
                .createdAt(LocalDateTime.now())
                .build());

        return "댓글 저장 완료";

    }

    // 댓글 목록 가져오기
    public SliceApiResponse<?> findList(Long projectId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        Slice<CommentResponseDto> sliceProject = commentRepository.findSliceByProjectId(projectId, pageable);

        return new SliceApiResponse<>(OK, sliceProject.getContent(), sliceProject.hasNext());

    }

    // 댓글 수정
    public String update(Long projectId, CommentRequestDto request, Authentication authentication) {

        Comment comment = commentRepository.findByCommentId(request.getCommentId());

        if (comment == null)
            throw new NullPointerException("해당 댓글은 존재하지 않습니다.");

        if (!(comment.getProject().getProjectId().equals(projectId) && comment.getUser().getUserId().equals(jwtService.getUserIdFromAuthentication(authentication))))
            throw new RuntimeException("프로젝트 번호와 댓글 작성자가 일치하지 않습니다.");

        comment.updateContent(request.getContent());
        comment.updateLastModifiedAt(LocalDateTime.now());

        return "댓글 수정 완료";

    }

    // 댓글 삭제
    public String delete(Long projectId, Long commentId, Authentication authentication) {

        Comment comment = commentRepository.findByCommentId(commentId);

        if (comment == null)
            throw new NullPointerException("해당 댓글은 존재하지 않습니다.");

        if (!(comment.getProject().getProjectId().equals(projectId) && comment.getUser().getUserId().equals(jwtService.getUserIdFromAuthentication(authentication))))
            throw new RuntimeException("프로젝트 번호와 댓글 작성자가 일치하지 않습니다.");

        commentRepository.delete(comment);

        return "댓글 삭제 완료";

    }

}