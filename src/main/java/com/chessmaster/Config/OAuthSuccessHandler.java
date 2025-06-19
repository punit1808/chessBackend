package com.chessmaster.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.persistence.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;


@Component
public class OAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("https://vite-frontend-gamma.vercel.app");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}








