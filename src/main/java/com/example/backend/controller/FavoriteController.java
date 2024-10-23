package com.example.backend.controller;

import com.example.backend.common.response.CommonApiResponse;
import com.example.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 프로젝트 찜하기
    @PostMapping("/projects/favorite")
    public CommonApiResponse<?> addProject(@RequestParam Long projectId, @AuthenticationPrincipal Long userId) {
        return new CommonApiResponse<>(OK, favoriteService.addProject(projectId, userId));
    }

    // 프로젝트 찜하기 취소
    @DeleteMapping("/projects/favorite")
    private CommonApiResponse<?> deleteProject(@RequestParam Long projectId, Authentication authentication) {
        return new CommonApiResponse<>(OK, favoriteService.deleteProject(projectId, authentication));
    }

    // 유저 찜하기
    @PostMapping("/users/favorite")
    public CommonApiResponse<?> addUser(@RequestParam Long favoriteId, Authentication authentication) {
        return new CommonApiResponse<>(OK, favoriteService.addUser(favoriteId, authentication));
    }

    // 유저 찜하기 취소
    @DeleteMapping("/users/favorite")
    private CommonApiResponse<?> deleteUser(@RequestParam Long favoriteId, Authentication authentication) {
        return new CommonApiResponse<>(OK, favoriteService.deleteUser(favoriteId, authentication));
    }

}
