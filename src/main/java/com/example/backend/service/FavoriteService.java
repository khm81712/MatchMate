package com.example.backend.service;

import com.example.backend.domain.Project;
import com.example.backend.domain.User;
import com.example.backend.repository.people.PeopleRepository;
import com.example.backend.repository.project.ProjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteService {

    private final ProjectRepository projectRepository;

    private final PeopleRepository peopleRepository;

    private final JwtService jwtService;

    // 프로젝트 찜하기
    public String projectFavorite(Long projectId, HttpServletRequest servletRequest) {

        Project project = projectRepository.findByProjectId(projectId);

        project.addProjectLike(jwtService.getUserIdFromToken(servletRequest));
        project.updateFavoriteCount(1);

        return "프로젝트 찜하기 완료";

    }

    // 프로젝트 찜하기 취소
    public String projectFavoriteCancel(Long projectId, HttpServletRequest servletRequest) {

        Project project = projectRepository.findByProjectId(projectId);

        project.getProjectLike().remove(jwtService.getUserIdFromToken(servletRequest));
        project.updateFavoriteCount(-1);

        return "프로젝트 찜하기 취소 완료";

    }

    public String userFavorite(Long favoriteId, HttpServletRequest servletRequest) {

        User user = peopleRepository.findUserByUserId(favoriteId);

        user.addUserLike(jwtService.getUserIdFromToken(servletRequest));
        user.updateFavoriteCount(1);

        return "유저 찜하기 완료";

    }

    public String userFavoriteCancel(Long favoriteId, HttpServletRequest servletRequest) {

        User user = peopleRepository.findUserByUserId(favoriteId);

        user.getUserLike().remove(jwtService.getUserIdFromToken(servletRequest));
        user.updateFavoriteCount(-1);

        return "유저 찜하기 취소 완료";

    }

}
