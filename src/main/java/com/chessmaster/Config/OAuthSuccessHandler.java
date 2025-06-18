package com.chessmaster.Config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.chessmaster.jwt.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public OAuthSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String token = jwtService.generateToken(email);

        // Create secure HTTP-only cookie
        // interchanged
        ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .domain("chessbackend-production.up.railway.app") // Add this
            .path("/")
            .maxAge(Duration.ofDays(1))
            .build();
        
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect("https://vite-frontend-gamma.vercel.app");

    }
}



