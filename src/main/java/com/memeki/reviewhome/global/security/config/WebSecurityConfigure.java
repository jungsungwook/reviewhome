package com.memeki.reviewhome.global.security.config;

import com.memeki.reviewhome.global.security.jwt.JwtAuthenticationFilter;
import com.memeki.reviewhome.global.security.jwt.JwtTokenProvider;
import com.memeki.reviewhome.global.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.memeki.reviewhome.global.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.memeki.reviewhome.global.security.repository.CookieAuthorizationRequestRepository;
import com.memeki.reviewhome.global.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfigure {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final JwtTokenProvider jwtTokenProvider;
        private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
        private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // httpBasic, csrf, formLogin, rememberMe, logout, session disable
                http
                                .cors()
                                .and()
                                .httpBasic().disable()
                                .csrf().disable()
                                .formLogin().disable()
                                .rememberMe().disable()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

                // 요청에 대한 권한 설정
                http.authorizeRequests(requests -> requests
                                .antMatchers("/oauth2/**", "/api/**").permitAll()
                                .anyRequest().authenticated());

                // oauth2Login
                http
                                .oauth2Login() // 소셜 로그인 페이지 없음
                                .loginPage("/api/auth/403")
                                .authorizationEndpoint()
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                                .and()
                                .redirectionEndpoint()
                                .baseUri("/oauth2/callback/*")
                                .and()
                                .userInfoEndpoint()
                                .userService(customOAuth2UserService)
                                .and()
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler);

                // http.logout()
                // .clearAuthentication(true)
                // .deleteCookies("JSESSIONID");

                // jwt filter 설정
                http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 클라이언트 도메인을 지정
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
                configuration.setAllowCredentials(true); // 필요한 경우, 자격 증명(쿠키 등)을 포함할지 여부 설정

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}