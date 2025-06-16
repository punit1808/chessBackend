package com.chessmaster.Controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TokenController {

    @GetMapping("/token")
    public ResponseEntity<?> getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No cookies present"));
        }
    
        String jwt = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName())) // FIXED: "token"
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    
        if (jwt == null) {
            return ResponseEntity.status(401).body(Map.of("error", "JWT token not found in cookies"));
        }
    
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @GetMapping("/postlogin")
    public ResponseEntity<String> postLogin(@RequestParam String token) {
        String html = """
        <html>
        <head>
            <script>
            document.cookie = "token=%s; path=/; secure; samesite=None";
            window.location.href = "https://chess-frontend-ashy.vercel.app";
            </script>
        </head>
        <body></body>
        </html>
        """.formatted(token);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html").body(html);
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

