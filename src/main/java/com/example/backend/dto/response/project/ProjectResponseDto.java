package com.example.backend.dto.response.project;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class ProjectResponseDto {

    private Long projectId;

    private String nickname;

    private String userFileUrl;

    private String title;

    private String techStack;

    private String position;

    private LocalDate deadline;

    private int viewCount;

    private int favoriteCount;

    private LocalDateTime createdAt;

    private Boolean recent;

    public void setRecent(Boolean recent) {
        this.recent = recent;
    }

    @QueryProjection
    public ProjectResponseDto(Long projectId, String nickname, String userFileUrl, String title, String techStack, String position, LocalDate deadline, int viewCount, int favoriteCount, LocalDateTime createdAt) {
        this.projectId = projectId;
        this.nickname = nickname;
        this.userFileUrl = userFileUrl;
        this.title = title;
        this.techStack = techStack;
        this.position = position;
        this.deadline = deadline;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public ProjectResponseDto(Long projectId, String nickname, String userFileUrl, String title, String techStack, String position, LocalDate deadline, LocalDateTime createdAt) {
        this.projectId = projectId;
        this.nickname = nickname;
        this.userFileUrl = userFileUrl;
        this.title = title;
        this.techStack = techStack;
        this.position = position;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

}
