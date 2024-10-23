package com.example.backend.dto.request.project;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ProjectRequestDto {

    private String title;

    private LocalDate deadline;

    private String techStack;

    private String softSkill;

    private String importantQuestion;

    private String description;

    private List<RecruitRequestDto> recruit;

}
