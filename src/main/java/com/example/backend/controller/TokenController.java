package com.example.backend.controller;

import com.example.backend.common.response.CommonApiResponse;
import com.example.backend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;

    /*
    @GetMapping("/oauth2/success")
    public CommonApiResponse<?> oAuth2Success(@RequestParam("accessToken") String accessToken,
                                                             @RequestParam("refreshToken") String refreshToken) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return new CommonApiResponse<>(OK, tokens);
    }
     */

    @GetMapping("/token")
    public CommonApiResponse<?> reissueAccessToken(HttpServletRequest request) {
        return new CommonApiResponse<>(OK, jwtService.reissueAccessToken(request));
    }

    @PostMapping("/token")
    public CommonApiResponse<?> addBlackList(HttpServletRequest request) {
        return new CommonApiResponse<>(OK, jwtService.addBlackList(request));
    }

}
