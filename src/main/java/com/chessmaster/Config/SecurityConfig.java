package com.chessmaster.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;

// import com.chessmaster.jwt.JwtAuthFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;


import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // private final JwtAuthFilter jwtAuthFilter;
    // private final OAuthSuccessHandler successHandler;

    // public SecurityConfig(JwtAuthFilter jwtAuthFilter, OAuthSuccessHandler successHandler) {
    //     this.jwtAuthFilter = jwtAuthFilter;
    //     this.successHandler = successHandler;
    // }



    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        DefaultOAuth2AuthorizationRequestResolver resolver =
            new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(builder ->
            builder.additionalParameters(params -> {
                params.put("prompt", "consent"); // or "select_account"
            })
        );
        return resolver;
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(List.of("https://chess-frontend-ashy.vercel.app"));
    //     configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     configuration.setAllowCredentials(true); // ✅ critical for cookies
    //     configuration.setAllowedHeaders(List.of("*"));

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }


    // from chessGame SecurityConfig
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    // .cors(Customizer.withDefaults()) // Enable CORS
    // .csrf().disable() // (optional) if you're not using CSRF protection
    // .authorizeHttpRequests()
    //     .requestMatchers("/logout", "/login/**", "/oauth2/**","/**","/token").permitAll()
    //     .anyRequest().authenticated()
    // .and()
    // .oauth2Login()
    // .defaultSuccessUrl("https://chess-frontend-ashy.vercel.app/Start", true)
    // .and()
    // .logout()
    //     .logoutUrl("/logout")
    //     .logoutSuccessHandler((request, response, authentication) -> {
    //         ResponseCookie cookie = ResponseCookie.from("token", "")
    //             .httpOnly(true)
    //             .path("/")
    //             .maxAge(0)
    //             .build();
    //         response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    //         response.setStatus(HttpServletResponse.SC_OK);
    //     });
    //     return http.build();
    // }



    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                 .allowedOrigins("https://chess-frontend-ashy.vercel.app")
    //                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //                 .allowCredentials(true)
    //                 .allowedHeaders("*")
    //                 .exposedHeaders("Set-Cookie"); // Important for cookies
    //         }
    //     };
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/login/**", "/oauth2/**", "/token", "/logout").permitAll()
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .defaultSuccessUrl("https://chess-frontend-ashy.vercel.app/Start", true)
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Optional: expire JSESSIONID
                    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                    response.setStatus(HttpServletResponse.SC_OK);
                });

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://chess-frontend-ashy.vercel.app"));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
