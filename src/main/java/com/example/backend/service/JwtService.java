package com.example.backend.service;

import com.example.backend.domain.TokenBlackList;
import com.example.backend.domain.User;
import com.example.backend.dto.oauth2.CustomOAuth2User;
import com.example.backend.repository.JwtRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtRepository jwtRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public String buildToken(CustomOAuth2User customOAuth2User, Long expiration, String type) {
        Date expiryDate = new Date(new Date().getTime() + expiration);

        return Jwts.builder()
                .setSubject(type)
                .claim("userId", customOAuth2User.getUser().getUserId())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String createAccessToken(CustomOAuth2User customOAuth2User) {
        return buildToken(customOAuth2User, accessExpiration, "A");
    }

    public String createRefreshToken(CustomOAuth2User customOAuth2User) {
        return buildToken(customOAuth2User, refreshExpiration, "R");
    }

    public Boolean validToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }

    }

    public String reissueAccessToken(HttpServletRequest request) {
        String refreshToken = resolveToken(request);
        Optional<TokenBlackList> jwtOptional = jwtRepository.findByToken(refreshToken);

        if (jwtOptional.isPresent())  // 블랙 리스트에 등록된 토큰인 경우
            return "유효한 토큰이 아닙니다.";

        if (!getClaimsFromToken(refreshToken).getSubject().equals("R"))
            return "리프레시 토큰이 아닙니다.";

        Claims claims = getClaimsFromToken(refreshToken);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                User.builder()
                        .userId(claims.get("userId", Long.class))
                        .build());

        return createAccessToken(customOAuth2User);
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        return new UsernamePasswordAuthenticationToken(claims.get("userId").toString(), null, null);
    }

    public String addBlackList(HttpServletRequest request) {
        String refreshToken = resolveToken(request);
        jwtRepository.save(TokenBlackList.builder().refreshToken(refreshToken).build());
        return "등록 성공";
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        return Long.parseLong(authentication.getPrincipal().toString());
    }

}
