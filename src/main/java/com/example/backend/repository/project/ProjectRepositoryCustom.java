package com.example.backend.repository.project;

import com.example.backend.dto.request.project.ProjectSearchDto;
import com.example.backend.dto.response.project.ProjectDetailResponseDto;
import com.example.backend.dto.response.project.ProjectResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectRepositoryCustom {

    List<ProjectDetailResponseDto> findDetailByProjectId(Long projectId);

    Page<ProjectResponseDto> findProjects(Pageable pageable, ProjectSearchDto searchDto);

    List<ProjectResponseDto> findHotProjects(int size);

    Page<ProjectResponseDto> findFavoriteProjects(Long userId, Pageable pageable);

    Page<ProjectResponseDto> findMyProjects(Long userId, Pageable pageable);

    List<ProjectResponseDto> findResponseDtoAllById(List<Long> projectIds);

}
