package com.memeki.reviewhome.auth.service;

import org.springframework.stereotype.Service;

import com.memeki.reviewhome.global.security.lib.CookieUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    public void signOut(
            HttpServletRequest request,
            HttpServletResponse response) {
        CookieUtils.deleteCookie(
                request,
                response,
                "token");
    }
}
