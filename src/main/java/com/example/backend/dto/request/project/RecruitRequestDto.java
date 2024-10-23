package com.example.backend.dto.request.project;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecruitRequestDto {

    private String position;

    private int currentCount;

    private int targetCount;

    @QueryProjection
    public RecruitRequestDto(String position, int currentCount, int targetCount) {
        this.position = position;
        this.currentCount = currentCount;
        this.targetCount = targetCount;
    }

}
