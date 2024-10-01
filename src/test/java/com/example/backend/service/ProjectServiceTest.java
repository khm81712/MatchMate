package com.example.backend.service;

import com.example.backend.common.response.PageApiResponse;
import com.example.backend.dto.request.project.ProjectRequestDto;
import com.example.backend.dto.request.project.ProjectSearchDto;
import com.example.backend.dto.request.project.RecruitRequestDto;
import com.example.backend.dto.response.project.ProjectDetailResponseDto;
import com.example.backend.dto.response.project.ProjectResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@Sql("/sql/service/project-service-test-insert.sql")
@TestPropertySource("classpath:application-test.properties")
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private HttpServletResponse response;

    @Test
    void 프로젝트_저장_사진이_있을_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                        .position("designer")
                        .targetCount(1)
                        .currentCount(0)
                .build());

        ProjectRequestDto dto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test image content".getBytes());

        //when
        String result = projectService.create(dto, file, authentication);

        //then
        assertEquals("프로젝트 저장 완료", result);
        verify(awsS3Service, times(1)).upload(any(MultipartFile.class));

    }

    @Test
    void 프로젝트_저장_사진이_없을_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                .position("designer")
                .targetCount(1)
                .currentCount(0)
                .build());

        ProjectRequestDto requestDto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        //when
        String result = projectService.create(requestDto, file, authentication);

        //then
        assertEquals("프로젝트 저장 완료", result);
        verify(awsS3Service, times(0)).upload(any(MultipartFile.class));

    }

    @Test
    void 프로젝트_목록_가져오기_값이_있을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        //when
        PageApiResponse<?> result = projectService.findList(request);

        //then
        assertEquals(2, result.getTotalElements());

    }

    @Test
    @Sql("/sql/service/project-service-test-delete.sql")
    void 프로젝트_목록_가져오기_값이_없을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        //when
        PageApiResponse<?> result = projectService.findList(request);

        //then
        assertTrue(((List<?>)result.getData()).isEmpty());

    }

    @Test
    void 프로젝트_목록_가져오기_필수_값_없을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder().build();

        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> projectService.findList(request));

    }

    @Test
    void 프로젝트_목록_가져오기_잘못된_범위_값_받았을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(-1)
                .size(0)
                .build();

        //when
        //then
        assertThrows(IllegalArgumentException.class, () -> projectService.findList(request));

    }

    @Test
    void 프로젝트_상세_정보_가져오기_값이_있을_때_조회수_증가() {

        //given
        Cookie cookie = new Cookie("project_view", "[2]");

        //when
        ProjectDetailResponseDto result = projectService.findById(1L, cookie, response);

        //then
        assertNotNull(result);
        assertEquals(1, result.getViewCount()); // 조회수 증가

    }

    @Test
    void 프로젝트_상세_정보_가져오기_값이_있을_때_조회수_증가_쿠키_X() {

        //given

        //when
        ProjectDetailResponseDto result = projectService.findById(1L, null, response);

        //then
        assertNotNull(result);
        assertEquals(1, result.getViewCount()); // 조회수 증가

    }

    @Test
    void 프로젝트_상세_정보_가져오기_값이_있을_때_조회수_증가_X() {

        //given
        Cookie cookie = new Cookie("project_view", "[1]_[2]");

        //when
        ProjectDetailResponseDto result = projectService.findById(1L, cookie, response);

        //then
        assertNotNull(result);
        assertEquals(0, result.getViewCount()); // 조회수 증가

    }

    @Test
    void 프로젝트_상세_정보_가져오기_값이_없을_때() {

        //given
        Cookie cookie = new Cookie("project_view", "[1]_[2]");

        //when
        ProjectDetailResponseDto result = projectService.findById(10L, cookie, response);

        //then
        assertNull(result);

    }

    @Test
    void 핫_프로젝트_상세_목록_가져오기_값이_있을_때() {

        //given

        //when
        List<ProjectResponseDto> result = projectService.findHotList(5);

        //then
        assertEquals(2, result.size());

    }

    @Test
    @Sql("/sql/service/project-service-test-delete.sql")
    void 핫_프로젝트_상세_목록_가져오기_값이_없을_때() {

        //given

        //when
        List<ProjectResponseDto> result = projectService.findHotList(5);

        //then
        assertTrue(result.isEmpty());

    }

    @Test
    void 내가_찜한_프로젝트_목록_가져오기_값이_있을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        PageApiResponse<?> result = projectService.findFavoriteList(authentication, request);

        //then
        assertEquals(1, result.getTotalElements());

    }

    @Test
    @Sql("/sql/service/project-service-test-delete.sql")
    void 내가_찜한_프로젝트_목록_가져오기_값이_없을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        PageApiResponse<?> result = projectService.findFavoriteList(authentication, request);

        //then
        assertTrue(((List<?>)result.getData()).isEmpty());

    }

    @Test
    void 내가_작성한_프로젝트_목록_가져오기_값이_있을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        PageApiResponse<?> result = projectService.findMyList(authentication, request);

        //then
        assertEquals(2, result.getTotalElements());

    }

    @Test
    @Sql("/sql/service/project-service-test-delete.sql")
    void 내가_작성한_프로젝트_목록_가져오기_값이_없을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .page(0)
                .size(5)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        PageApiResponse<?> result = projectService.findMyList(authentication, request);

        //then
        assertTrue(((List<?>)result.getData()).isEmpty());

    }

    @Test
    void 프로젝트_수정_사진이_있을_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                .position("designer")
                .targetCount(1)
                .currentCount(0)
                .build());

        ProjectRequestDto dto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test image content".getBytes());

        //when
        String result = projectService.update(1L, dto, file, authentication);

        //then
        assertEquals("프로젝트 수정 완료", result);
        verify(awsS3Service, times(1)).upload(any(MultipartFile.class));

    }

    @Test
    void 프로젝트_수정_사진이_없을_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                .position("designer")
                .targetCount(1)
                .currentCount(0)
                .build());

        ProjectRequestDto requestDto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        //when
        String result = projectService.update(1L, requestDto, file, authentication);

        //then
        assertEquals("프로젝트 수정 완료", result);
        verify(awsS3Service, times(0)).upload(any(MultipartFile.class));

    }

    @Test
    void 프로젝트_수정_작성자가_아닐_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                .position("designer")
                .targetCount(1)
                .currentCount(0)
                .build());

        ProjectRequestDto requestDto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(2L, null, null);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        //when
        //then
        assertThrows(RuntimeException.class, () -> projectService.update(1L, requestDto, file, authentication));

    }

    @Test
    void 프로젝트_수정_프로젝트가_없을_때() throws IOException {

        //given
        List<RecruitRequestDto> recruits = new ArrayList<>();

        recruits.add(RecruitRequestDto.builder()
                .position("designer")
                .targetCount(1)
                .currentCount(0)
                .build());

        ProjectRequestDto requestDto = ProjectRequestDto.builder()
                .title("프로젝트 제목3")
                .deadline(LocalDate.parse("2024-09-20"))
                .techStack("figma")
                .softSkill("시간 관리")
                .importantQuestion("주 1회 회의")
                .description("프로젝트 설명3")
                .recruit(recruits)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(2L, null, null);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);

        //when
        //then
        assertThrows(NullPointerException.class, () -> projectService.update(10L, requestDto, file, authentication));

    }

    @Test
    void 프로젝트_삭제_사진이_있을_때() throws IOException {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        String result = projectService.delete(1L, authentication);

        //then
        assertEquals("프로젝트 삭제 완료", result);
        verify(awsS3Service, times(1)).deleteFileFromS3(any(String.class));

    }

    @Test
    void 프로젝트_삭제_사진이_없을_때() throws IOException {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, null);

        //when
        String result = projectService.delete(2L, authentication);

        //then
        assertEquals("프로젝트 삭제 완료", result);
        verify(awsS3Service, times(0)).deleteFileFromS3(any(String.class));

    }

    @Test
    void 프로젝트_삭제_작성자가_아닐_때() throws IOException {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(2L, null, null);

        //when
        //then
        assertThrows(RuntimeException.class, () -> projectService.delete(1L, authentication));

    }

    @Test
    void 프로젝트_삭제_프로젝트가_없을_때() throws IOException {

        //given
        Authentication authentication = new UsernamePasswordAuthenticationToken(2L, null, null);

        //when
        //then
        assertThrows(NullPointerException.class, () -> projectService.delete(10L, authentication));

    }

}
