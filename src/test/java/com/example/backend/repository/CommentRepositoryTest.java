package com.example.backend.repository;

import com.example.backend.common.config.JpaTestConfig;
import com.example.backend.domain.Comment;
import com.example.backend.dto.response.comment.CommentResponseDto;
import com.example.backend.repository.comment.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@Sql("/sql/repository/comment-repository-test-insert.sql")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByCommentId_값이_있을_때() {

        //given

        //when
        Comment result = commentRepository.findByCommentId(1L);

        //then
        assertEquals(1L, result.getCommentId());
        assertEquals("댓글 내용1", result.getContent());
        assertEquals(1L, result.getUser().getUserId());
        assertEquals(1L, result.getProject().getProjectId());
        assertNull(result.getLastModifiedAt());
        assertEquals(LocalDateTime.parse("2024-08-10T09:00:00"), result.getCreatedAt());

    }

    @Test
    @Sql("/sql/repository/comment-repository-test-delete.sql")
    void findByCommentId_값이_없을_때() {

        //given

        //when
        Comment result = commentRepository.findByCommentId(1L);

        //then
        assertNull(result);

    }

    @Test
    void findSliceByProjectId_값이_있을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Slice<CommentResponseDto> result = commentRepository.findSliceByProjectId(1L, pageable);

        //then
        assertTrue(result.getContent().size() == 2);

    }

    @Test
    @Sql("/sql/repository/comment-repository-test-delete.sql")
    void findSliceByProjectId_값이_없을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Slice<CommentResponseDto> result = commentRepository.findSliceByProjectId(1L, pageable);

        //then
        assertTrue(result.getContent().isEmpty());

    }

}
