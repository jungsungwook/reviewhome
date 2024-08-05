package com.memeki.reviewhome.auth.controller;

import org.springframework.web.bind.annotation.RestController;

import com.memeki.reviewhome.auth.dto.CheckAuthenticationResponseDto;
import com.memeki.reviewhome.auth.service.AuthService;
import com.memeki.reviewhome.global.exception.DefaultException;
import com.memeki.reviewhome.global.exceptionHandler.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @GetMapping("/403")
    public String forbidden() throws Exception{
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

    @GetMapping(value = "/check")
    @PreAuthorize("isAuthenticated()")
    public CheckAuthenticationResponseDto checkAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 성공 시 200
        response.setStatus(HttpServletResponse.SC_OK);
        CheckAuthenticationResponseDto responseDto = new CheckAuthenticationResponseDto();
        responseDto.setMessage("success");
        responseDto.setStatus(HttpServletResponse.SC_OK);
        return responseDto;
    }
    

}
