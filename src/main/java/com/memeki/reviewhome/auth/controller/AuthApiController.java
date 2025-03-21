package com.memeki.reviewhome.auth.controller;

import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.auth.dto.CheckAuthenticationResponseDto;
import com.memeki.reviewhome.auth.service.AuthService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;
import com.memeki.reviewhome.global.security.entity.User;
import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;
import com.memeki.reviewhome.global.security.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "AuthApiController", description = "인증 관련 API 컨트롤러")
@RequestMapping(value = "/api/auth")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @GetMapping("/403")
    public String forbidden() throws Exception {
        throw new DefaultException(ErrorCode.FORBIDDEN);
    }

    @GetMapping(value = "/signin")
    public String getMethodName(@RequestParam String param) throws Exception {
        System.out.println("param = " + param);
        return param;
    }

    @GetMapping(value = "/signup")
    @PreAuthorize("isAuthenticated()")
    public String getMethodName2(@RequestParam String param) throws Exception {
        System.out.println("param = " + param);
        return param + "signup";
    }

    @GetMapping(value = "/signout")
    @PreAuthorize("isAuthenticated()")
    public String signOut(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response) throws Exception {
        // 쿠키값 "token"을 삭제
        authService.signOut(
                request,
                response);

        return "redirect:/";
    }

    @GetMapping(value = "/check")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "인증 확인", description = "현재 로그인한 사용자의 인증 정보를 확인합니다.")
    public CheckAuthenticationResponseDto checkAuthentication(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userDetails)
            throws Exception {
        User user = userService.getUserById(userDetails.getId());
        CheckAuthenticationResponseDto responseDto = new CheckAuthenticationResponseDto();
        responseDto.setStatus(HttpServletResponse.SC_OK);
        responseDto.setUserId(user.getId());
        responseDto.setUserName(user.getName());
        responseDto.setNickname(user.getNickname());
        return responseDto;
    }

}
