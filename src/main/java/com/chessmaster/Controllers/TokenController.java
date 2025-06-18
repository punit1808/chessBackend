package com.chessmaster.Controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TokenController {

    // @GetMapping("/token")
    // public ResponseEntity<?> getTokenFromCookie(HttpServletRequest request) {
    //     Cookie[] cookies = request.getCookies();
        
    //     if (cookies == null) {
    //         return ResponseEntity.status(401).body(Map.of("error", "No cookies present"));
    //     }

    //     String jwt = Arrays.stream(cookies)
    //             .filter(cookie -> "token".equals(cookie.getName()))
    //             .findFirst()
    //             .map(Cookie::getValue)
    //             .orElse(null);

    //     if (jwt == null) {
    //         return ResponseEntity.status(401).body(Map.of("error", "JWT token not found in cookies"));
    //     }

    //     return ResponseEntity.ok(Map.of("token", jwt));
    // }

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getTokenInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                                .body(Map.of("error", "Not authenticated"));
        }

        String email = authentication.getName(); // or extract custom claims if needed

        return ResponseEntity.ok(Map.of("email", email));
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .secure(true)
            .sameSite("none")
            .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logged out successfully");
    }

}

