package com.example.backend.dto.request.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRequestDto {

    private Long commentId;

    private String content;

}
