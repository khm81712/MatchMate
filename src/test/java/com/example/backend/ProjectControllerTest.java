package com.example.backend;

import com.example.backend.domain.User;
import com.example.backend.dto.oauth2.CustomOAuth2User;
import com.example.backend.repository.people.PeopleRepository;
import com.example.backend.service.JwtService;
import com.example.backend.service.ProjectService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback
public class ProjectControllerTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    JwtService jwtProvider;

    @Autowired
    PeopleRepository peopleRepository;

    @Test
    void postProject() {



    }

    @Test
    public void jwtTest() {

        User user = peopleRepository.findUserByUserId(1L);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user, "kakao");
        String accessToken = jwtProvider.createAccessToken(customOAuth2User);
        String refreshToken = jwtProvider.createRefreshToken(customOAuth2User);
        Claims accessClaims = jwtProvider.getClaimsFromToken(accessToken);


        System.out.println("accessToken : " + accessToken);
        System.out.println("==============");
        System.out.println("refreshToken : " + refreshToken);

    }

}
