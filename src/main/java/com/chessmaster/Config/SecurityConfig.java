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

import com.chessmaster.jwt.JwtAuthFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuthSuccessHandler successHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, OAuthSuccessHandler successHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.successHandler = successHandler;
    }



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



// new filter
// @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//     http
//         .csrf().disable()
//         .authorizeHttpRequests(auth -> auth
//             .requestMatchers("/logout", "/login/**", "/oauth2/**", "/token", "/postlogin").permitAll()
//             .anyRequest().authenticated()
//         )
//         .oauth2Login(o -> o
//             .successHandler(successHandler)
//             .authorizationEndpoint()
//                 .authorizationRequestResolver(customAuthorizationRequestResolver(null))
//         )
//         .logout(logout -> logout
//             .logoutUrl("/logout")
//             .logoutSuccessHandler((request, response, authentication) -> {
//                 ResponseCookie cookie = ResponseCookie.from("token", "")
//                     .httpOnly(true)
//                     .secure(true)
//                     .sameSite("None")
//                     .domain("chessbackend-production.up.railway.app")
//                     .path("/")
//                     .maxAge(0)
//                     .build();
//                 response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//                 response.setStatus(HttpServletResponse.SC_OK);
//             })
//         );

//     http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//     return http.build();
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
            .requestMatchers("/logout", "/login/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
        .and()
        .oauth2Login()
            .defaultSuccessUrl("https://chess-frontend-ashy.vercel.app/Start", true)
        .and()
        .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler((request, response, authentication) -> {
                ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();
                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            });

    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("https://chess-frontend-ashy.vercel.app"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Set-Cookie"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}


}
