package com.chessmaster.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class securityConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("https://chess-frontend-ashy.vercel.app","https://chess-frontend-punit1808s-projects.vercel.app","https://chess-frontend-git-main-punit1808s-projects.vercel.app")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true)
                    .allowedHeaders("*");
            }
        };
    }
}
