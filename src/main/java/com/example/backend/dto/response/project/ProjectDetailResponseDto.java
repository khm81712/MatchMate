package com.example.backend.dto.response.project;

import com.example.backend.dto.request.project.RecruitRequestDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProjectDetailResponseDto {

    private Long projectId;

    private Long userId;

    private String nickname;

    private String userFileUrl;

    private String projectFileUrl;

    private String title;

    private String techStack;

    private String softSkill;

    private String importantQuestion;

    private LocalDate deadline;

    private String recruitment;

    private Boolean employmentStatus;

    private int viewCount;

    private int favoriteCount;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    private List<RecruitRequestDto> recruits;

    @QueryProjection
    public ProjectDetailResponseDto(Long projectId, Long userId, String nickname, String userFileUrl, String projectFileUrl, String title, String techStack, String softSkill, String importantQuestion, LocalDate deadline, String recruitment, Boolean employmentStatus, int viewCount, int favoriteCount, String description, LocalDateTime createdAt, LocalDateTime lastModifiedAt, List<RecruitRequestDto> recruits) {
        this.projectId = projectId;
        this.userId = userId;
        this.nickname = nickname;
        this.userFileUrl = userFileUrl;
        this.projectFileUrl = projectFileUrl;
        this.title = title;
        this.techStack = techStack;
        this.softSkill = softSkill;
        this.importantQuestion = importantQuestion;
        this.deadline = deadline;
        this.recruitment = recruitment;
        this.employmentStatus = employmentStatus;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.description = description;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.recruits = recruits;
    }

}
