package com.example.backend.repository;

import com.example.backend.common.config.JpaTestConfig;
import com.example.backend.domain.*;
import com.example.backend.dto.request.project.ProjectSearchDto;
import com.example.backend.dto.response.project.ProjectDetailResponseDto;
import com.example.backend.dto.response.project.ProjectResponseDto;
import com.example.backend.repository.project.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@Sql("/sql/repository/project-repository-test-insert.sql")
@TestPropertySource("classpath:application-test.properties")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findByProjectId_값이_있을_때() {

        //given

        //when
        Project result = projectRepository.findByProjectId(1L);

        //then
        assertEquals(1L, result.getProjectId());
        assertEquals(1L, result.getUser().getUserId());
        assertEquals("프로젝트 제목1", result.getTitle());
        assertEquals("project_image.png", result.getProjectFileUrl());
        assertEquals(LocalDate.parse("2024-09-20"), result.getDeadline());
        assertEquals("OPEN", result.getRecruitment());
        assertEquals("vue, typescript", result.getTechStack());
        assertEquals("frontend", result.getPosition());
        assertEquals("시간 관리", result.getSoftSkill());
        assertEquals("주 1회 회의", result.getImportantQuestion());
        assertEquals(0, result.getViewCount());
        assertEquals(0, result.getFavoriteCount());
        assertEquals("프로젝트 설명1", result.getDescription());
        assertNull(result.getLastModifiedAt());
        assertEquals(LocalDateTime.parse("2024-08-10T09:00:00"), result.getCreatedAt());
        assertEquals(1L, result.getRecruits().get(0).getProject().getProjectId());
        assertEquals(0, result.getRecruits().get(0).getCurrentCount());
        assertEquals(1, result.getRecruits().get(0).getTargetCount());
        assertEquals("frontend", result.getRecruits().get(0).getPosition());

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findByProjectId_값이_없을_때() {

        //given

        //when
        Project result = projectRepository.findByProjectId(1L);

        //then
        assertNull(result);

    }

    @Test
    void findByUserUserIdAndProjectId_값이_있을_때() {

        //given

        //when
        Project result = projectRepository.findByUserUserIdAndProjectId(1L, 1L);

        //then
        assertEquals(1L, result.getProjectId());
        assertEquals(1L, result.getUser().getUserId());
        assertEquals("프로젝트 제목1", result.getTitle());
        assertEquals("project_image.png", result.getProjectFileUrl());
        assertEquals(LocalDate.parse("2024-09-20"), result.getDeadline());
        assertEquals("OPEN", result.getRecruitment());
        assertEquals("vue, typescript", result.getTechStack());
        assertEquals("frontend", result.getPosition());
        assertEquals("시간 관리", result.getSoftSkill());
        assertEquals("주 1회 회의", result.getImportantQuestion());
        assertEquals(0, result.getViewCount());
        assertEquals(0, result.getFavoriteCount());
        assertEquals("프로젝트 설명1", result.getDescription());
        assertNull(result.getLastModifiedAt());
        assertEquals(LocalDateTime.parse("2024-08-10T09:00:00"), result.getCreatedAt());
        assertEquals(1L, result.getRecruits().get(0).getProject().getProjectId());
        assertEquals(0, result.getRecruits().get(0).getCurrentCount());
        assertEquals(1, result.getRecruits().get(0).getTargetCount());
        assertEquals("frontend", result.getRecruits().get(0).getPosition());

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findByUserUserIdAndProjectId_값이_없을_때() {

        //given

        //when
        Project result = projectRepository.findByUserUserIdAndProjectId(1L, 1L);

        //then
        assertNull(result);

    }

    @Test
    void updateViewCount_조회수를_올릴_수_있다() {

        //given

        //when
        projectRepository.updateViewCount(1L);
        Project result = projectRepository.findByProjectId(1L);

        //then
        assertEquals(1L, result.getProjectId());
        assertEquals(1, result.getViewCount());

    }

    @Test
    void findDetailByProjectId_값이_있을_때() {

        //given

        //when
        ProjectDetailResponseDto result = projectRepository.findDetailByProjectId(1L).get(0);

        //then
        assertEquals(1L, result.getProjectId());
        assertEquals(1L, result.getUserId());
        assertEquals("test_user", result.getNickname());
        assertEquals("user_image.png", result.getUserFileUrl());
        assertEquals("프로젝트 제목1", result.getTitle());
        assertEquals("project_image.png", result.getProjectFileUrl());
        assertEquals(LocalDate.parse("2024-09-20"), result.getDeadline());
        assertEquals("OPEN", result.getRecruitment());
        assertEquals("vue, typescript", result.getTechStack());
        assertEquals("시간 관리", result.getSoftSkill());
        assertEquals("주 1회 회의", result.getImportantQuestion());
        assertEquals(0, result.getViewCount());
        assertEquals(0, result.getFavoriteCount());
        assertEquals("프로젝트 설명1", result.getDescription());
        assertNull(result.getLastModifiedAt());
        assertEquals(LocalDateTime.parse("2024-08-10T09:00:00"), result.getCreatedAt());
        assertEquals(0, result.getRecruits().get(0).getCurrentCount());
        assertEquals(1, result.getRecruits().get(0).getTargetCount());
        assertEquals("frontend", result.getRecruits().get(0).getPosition());

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findDetailByProjectId_값이_없을_때() {

        //given

        //when
        List<ProjectDetailResponseDto> result = projectRepository.findDetailByProjectId(1L);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void findProjects_값이_있을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("")
                .sort("")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertTrue(result.getTotalElements() == 2);

    }

    @Test
    void findProjects_keyword_검색() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("1")
                .techStack("")
                .sort("")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertTrue(result.getTotalElements() == 1);

    }

    @Test
    void findProjects_techStack_검색() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("vue")
                .sort("")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertTrue(result.getTotalElements() == 1);

    }

    @Test
    void findProjects_position_검색() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("")
                .sort("")
                .position("frontend")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertTrue(result.getTotalElements() == 1);

    }

    @Test
    void findProjects_최신순_검색() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("")
                .sort("CreatedAt")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertEquals(1L, result.getContent().get(0).getProjectId());

    }

    @Test
    void findProjects_인기순_검색() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("")
                .sort("POPULAR")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertEquals(2L, result.getContent().get(0).getProjectId());

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findProjects_값이_없을_때() {

        //given
        ProjectSearchDto request = ProjectSearchDto.builder()
                .size(5)
                .page(0)
                .keyword("")
                .techStack("")
                .sort("")
                .position("")
                .build();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        //when
        Page<ProjectResponseDto> result = projectRepository.findProjects(pageable, request);

        //then
        assertTrue(result.getContent().isEmpty());

    }

    @Test
    void findHotProjects_값이_있을_때() {

        //given

        //when
        List<ProjectResponseDto> result = projectRepository.findHotProjects(5);

        //then
        assertTrue(result.size() == 2);

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findHotProjects_값이_없을_때() {

        //given

        //when
        List<ProjectResponseDto> result = projectRepository.findHotProjects(5);

        //then
        assertTrue(result.isEmpty());

    }

    @Test
    void findFavoriteProjects_값이_있을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<ProjectResponseDto> result = projectRepository.findFavoriteProjects(1L, pageable);

        //then
        assertTrue(result.getContent().size() == 1);

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findFavoriteProjects_값이_없을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<ProjectResponseDto> result = projectRepository.findFavoriteProjects(1L, pageable);

        //then
        assertTrue(result.getContent().isEmpty());

    }

    @Test
    void findMyProjects_값이_있을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<ProjectResponseDto> result = projectRepository.findMyProjects(1L, pageable);

        //then
        assertTrue(result.getContent().size() == 2);

    }

    @Test
    @Sql("/sql/repository/project-repository-test-delete.sql")
    void findMyProjects_값이_없을_때() {

        //given
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<ProjectResponseDto> result = projectRepository.findMyProjects(1L, pageable);

        //then
        assertTrue(result.getContent().isEmpty());

    }

}
