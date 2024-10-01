package com.example.backend.service;

import com.example.backend.domain.User;
import com.example.backend.dto.oauth2.CustomOAuth2User;
import com.example.backend.dto.oauth2.OAuthAttributes;
import com.example.backend.repository.people.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final PeopleRepository peopleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2 서비스 id (구글, 카카오, 네이버)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
        String userNameAttributeName = oAuth2User.getName();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);

        return new CustomOAuth2User(user, userNameAttributeName);

    }

    // 유저 생성 및 수정 서비스 로직
    private User saveOrUpdate(OAuthAttributes attributes){
        String nickname = attributes.getNickname();

        String picture = Optional.ofNullable(attributes.getPicture()).orElse("https://imagebucket0515.s3.ap-northeast-2.amazonaws.com/default_images_0520.jpg");
        String email = Optional.ofNullable(attributes.getEmail()).orElse(null);
        User userOptional = peopleRepository.findBySocialId(attributes.getNameAttributeKey()).orElse(null);

        // 유저가 등록 되지 않은 상태
        if (userOptional == null) {
            // 닉네임 중복 검증, 새로운 닉네임 부여
            if (peopleRepository.findByNickname(nickname) != null) {
                nickname += (int)(Math.random() * 10000000 + 1);
            }

            User user = User.builder()
                    .socialId(attributes.getNameAttributeKey())
                    .socialType(attributes.getRegistrationId())
                    .userFileUrl(picture)
                    .email(email)
                    .createdAt(LocalDateTime.now())
                    .nickname(nickname)
                    .year("0년")
                    .alarmStatus(false)
                    .employmentStatus(false)
                    .build();

            peopleRepository.save(user);

            return user;

        }

        return userOptional;

    }

}
