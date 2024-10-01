package com.example.backend.service;

import com.example.backend.common.response.SliceApiResponse;
import com.example.backend.dto.request.project.CommentRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql("/sql/service/comment-service-test-insert.sql")
@TestPropertySource("classpath:application-test.properties")
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void 댓글_저장() {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        String result = commentService.create(1L, "댓글 내용", authentication);

        //then
        assertEquals("댓글 저장 완료", result);

    }

    @Test
    void 댓글_목록_가져오기_값이_있을_때() {

        //given

        //when
        SliceApiResponse<?> result = commentService.findList(1L, 0, 5);

        //then
        assertEquals(2, ((List<?>)result.getData()).size());

    }

    @Test
    @Sql("/sql/service/comment-service-test-delete.sql")
    void 댓글_목록_가져오기_값이_없을_때() {

        //given

        //when
        SliceApiResponse<?> result = commentService.findList(1L, 0, 5);

        //then
        assertTrue(((List<?>)result.getData()).isEmpty());

    }

    @Test
    void 댓글_수정() {

        //given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .commentId(1L)
                .content("댓글 내용 수정")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        String result = commentService.update(1L, requestDto, authentication);

        //then
        assertEquals("댓글 수정 완료", result);

    }

    @Test
    void 댓글_수정_댓글이_없을_때() {

        //given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .commentId(10L)
                .content("댓글 내용 수정")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        //then
        assertThrows(NullPointerException.class, () -> commentService.update(1L, requestDto, authentication));

    }

    @Test
    void 댓글_수정_작성자가_아닐_때() {

        //given
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .commentId(1L)
                .content("댓글 내용 수정")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(2L, null, null);

        //when
        //then
        assertThrows(RuntimeException.class, () -> commentService.update(1L, requestDto, authentication));

    }

    @Test
    void 댓글_삭제() {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        String result = commentService.delete(1L, 1L, authentication);

        //then
        assertEquals("댓글 삭제 완료", result);

    }

    @Test
    void 댓글_삭제_댓글이_없을_때() {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        //then
        assertThrows(NullPointerException.class, () -> commentService.delete(1L, 10L, authentication));

    }

    @Test
    void 댓글_삭제_작성자가_아닐_때() {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        //then
        assertThrows(RuntimeException.class, () -> commentService.delete(1L, 10L, authentication));

    }

}
