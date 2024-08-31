package com.example.backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.backend.common.response.PageApiResponse;
import com.example.backend.domain.Project;
import com.example.backend.domain.Recruit;
import com.example.backend.domain.User;
import com.example.backend.dto.request.project.ProjectRequestDto;
import com.example.backend.dto.request.project.ProjectSearchDto;
import com.example.backend.dto.request.project.RecruitRequestDto;
import com.example.backend.dto.response.project.ProjectDetailResponseDto;
import com.example.backend.dto.response.project.ProjectResponseDto;
import com.example.backend.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AmazonS3Client amazonS3Client;
    private final AwsS3Service awsS3Service;
    private final JwtService jwtService;

    // 프로젝트 저장
    public String create(ProjectRequestDto dto, MultipartFile file, Authentication authentication) throws IOException {

        List<Recruit> recruits = new ArrayList<>();
        StringBuilder positionBuilder = new StringBuilder();
        String fileUrl = "";

        if(!file.isEmpty() && file != null) {
            fileUrl = awsS3Service.upload(file);
        }

        Project project = Project.builder()
                .user(User.builder().userId(jwtService.getUserIdFromAuthentication(authentication)).build())
                .title(dto.getTitle())
                .projectFileUrl(fileUrl)
                .deadline(dto.getDeadline())
                .softSkill(dto.getSoftSkill())
                .importantQuestion(dto.getImportantQuestion())
                .techStack(dto.getTechStack())
                .description(dto.getDescription())
                .recruits(recruits)
                .recruitment("OPEN")
                .createdAt(LocalDateTime.now())
                .build();

        String position = addRecruitsAndGetPositionCsv(project, dto, recruits, positionBuilder);

        project.updatePosition(position);
        project.updateRecruit(recruits);

        projectRepository.save(project);

        return "프로젝트 저장 완료";

    }

    // 프로젝트 목록 가져오기
    public PageApiResponse<?> findList(ProjectSearchDto request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ProjectResponseDto> projectPage = projectRepository.findProjects(pageable, request);

        List<ProjectResponseDto> projects = checkRecent(projectPage.getContent());

        return new PageApiResponse<>(OK, projects, projectPage.getTotalPages(), projectPage.getTotalElements());

    }

    // 프로젝트 상세 정보 가져오기
    public ProjectDetailResponseDto findById(Long projectId) {

        projectRepository.updateViewCount(projectId);

        List<ProjectDetailResponseDto> content = projectRepository.findDetailByProjectId(projectId);

        return (content.isEmpty()) ? null : content.get(0);

    }

    // 핫 프로젝트 목록 가져오기
    public List<ProjectResponseDto> findHotList(int size) {
        return checkRecent(projectRepository.findHotProjects(size));
    }

    // 내가 찜한 프로젝트 목록 가져오기
    public PageApiResponse<?> findFavoriteList(Authentication authentication, ProjectSearchDto request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ProjectResponseDto> projectPage = projectRepository.findFavoriteProjects(jwtService.getUserIdFromAuthentication(authentication), pageable);

        List<ProjectResponseDto> projects = checkRecent(projectPage.getContent());

        return new PageApiResponse<>(OK, projects, projectPage.getTotalPages(), projectPage.getTotalElements());

    }

    // 내가 작성한 프로젝트 가져오기
    public PageApiResponse<?> findMyList(Authentication authentication, ProjectSearchDto request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ProjectResponseDto> projectPage = projectRepository.findMyProjects(jwtService.getUserIdFromAuthentication(authentication), pageable);

        List<ProjectResponseDto> projects = checkRecent(projectPage.getContent());

        return new PageApiResponse<>(OK, projects, projectPage.getTotalPages(), projectPage.getTotalElements());

    }

    // 프로젝트 수정
    public String update(Long projectId, ProjectRequestDto dto, MultipartFile file,
                         Authentication authentication) throws IOException {

        Project project = projectRepository.findByProjectId(projectId);
        List<Recruit> recruits = project.getRecruits();
        StringBuilder positionBuilder = new StringBuilder();
        String fileUrl = "";

        if(!(project.getUser().getUserId() == jwtService.getUserIdFromAuthentication(authentication)))
            throw new RuntimeException("프로젝트 작성자가 아닙니다.");

        if(!file.isEmpty() && file != null) {
            awsS3Service.deleteFileFromS3(project.getProjectFileUrl()); //기존 파일 삭제
            fileUrl = awsS3Service.upload(file);
        }

        if (!recruits.isEmpty()) recruits.clear();

        String position = addRecruitsAndGetPositionCsv(project, dto, recruits, positionBuilder);

        project.update(dto.getTitle(), fileUrl, dto.getDeadline(),
                dto.getImportantQuestion(), dto.getSoftSkill(),
                dto.getTechStack(), dto.getDescription(),
                recruits, position, LocalDateTime.now());

        return "프로젝트 수정 완료";

    }

    public String delete(Long projectId, Authentication authentication) {

        Project project = projectRepository.findByUserUserIdAndProjectId(jwtService.getUserIdFromAuthentication(authentication), projectId);

        if(project == null) {
            throw new RuntimeException("해당 프로젝트는 존재하지 않습니다.");
        }
        if(project.getProjectFileUrl() != null && !project.getProjectFileUrl().isEmpty()) {
            awsS3Service.deleteFileFromS3(project.getProjectFileUrl());
        }

        projectRepository.delete(project);

        return "프로젝트 삭제 완료";

    }

    // 신규 스티커 여부 (생성한 후 1주일)
    private List<ProjectResponseDto> checkRecent(List<ProjectResponseDto> projects) {
        for (ProjectResponseDto project : projects) {
            boolean recent = !project.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1));
            project.setRecent(recent);
        }
        return projects;
    }

    // Recuruit 저장, PositionCsv 반환
    private String addRecruitsAndGetPositionCsv(Project project, ProjectRequestDto dto, List<Recruit> recruits, StringBuilder positionBuilder) {

        for (RecruitRequestDto recruitDto : dto.getRecruit()) {
            recruits.add(Recruit.builder()
                    .project(project)
                    .position(recruitDto.getPosition())
                    .currentCount(recruitDto.getCurrentCount())
                    .targetCount(recruitDto.getTargetCount())
                    .build());
            positionBuilder.append(recruitDto.getPosition()).append(", ");
        }

        return positionBuilder.length() > 0 ? positionBuilder.substring(0, positionBuilder.length() - 2) : "";

    }

}
