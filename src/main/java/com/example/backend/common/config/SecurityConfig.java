package com.example.backend.common.config;

import com.example.backend.common.filter.TokenAuthFilter;
import com.example.backend.common.handler.OAuth2SuccessHandler;
import com.example.backend.service.CustomOauth2UserService;
import com.example.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService oAuth2UserService;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers -> headers
                        .addHeaderWriter(((request, response) -> {
                            response.setHeader("Content-Security-Policy","default-src 'self' https://match-mate.store;");
                        })))

                .formLogin(formLogin -> formLogin.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))

                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/projects"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/projects/**"), // /projects/hot 포함
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/comments/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/peoples"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/peoples/**"), // /peoples/hot 포함
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/users/nickname"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/scheduled-test"),
                                AntPathRequestMatcher.antMatcher("/token"),
                                AntPathRequestMatcher.antMatcher("/favicon.ico"),
                                AntPathRequestMatcher.antMatcher("/error")
                        ).permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(new TokenAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(Arrays.asList("https://match-mate.store", "http://localhost:3000"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}