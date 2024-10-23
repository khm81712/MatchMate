package com.example.backend.service;

import com.example.backend.domain.Project;
import com.example.backend.domain.User;
import com.example.backend.repository.people.PeopleRepository;
import com.example.backend.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    public String addProject(Long projectId, Long userId) {

        Project project = projectRepository.findByProjectId(projectId);

        project.addProjectLike(userId);
        project.updateFavoriteCount(1);

        return "프로젝트 찜하기 완료";

    }

    // 프로젝트 찜하기 취소
    public String deleteProject(Long projectId, Authentication authentication) {

        Project project = projectRepository.findByProjectId(projectId);

        project.getProjectLike().remove(jwtService.getUserIdFromAuthentication(authentication));
        project.updateFavoriteCount(-1);

        return "프로젝트 찜하기 취소 완료";

    }

    public String addUser(Long favoriteId, Authentication authentication) {

        User user = peopleRepository.findUserByUserId(favoriteId);

        user.addUserLike(jwtService.getUserIdFromAuthentication(authentication));
        user.updateFavoriteCount(1);

        return "유저 찜하기 완료";

    }

    public String deleteUser(Long favoriteId, Authentication authentication) {

        User user = peopleRepository.findUserByUserId(favoriteId);

        user.getUserLike().remove(jwtService.getUserIdFromAuthentication(authentication));
        user.updateFavoriteCount(-1);

        return "유저 찜하기 취소 완료";

    }

}
