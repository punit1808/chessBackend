package com.chessmaster.Config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.context.NullSecurityContextRepository;

import com.chessmaster.jwt.JwtAuthFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

    // @Bean
    // public ServletWebServerFactory servletContainer() {
    //     TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    //     factory.addContextCustomizers(context -> {
    //         context.setUseHttpOnly(true);
    //     });
    //     return factory;
    // }


    //  @Bean
    // public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
    //     return factory -> factory.addContextCustomizers(context -> {
    //         Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
    //         cookieProcessor.setSameSiteCookies("None"); // 🟢 Critical
    //         context.setCookieProcessor(cookieProcessor);
    //     });
    // }

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .securityContext(context -> context
    //             .securityContextRepository(new NullSecurityContextRepository())
    //         )
    //         .sessionManagement(session -> session
    //             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //         )
    //         .cors(Customizer.withDefaults())
    //         .csrf().disable()
    //         .authorizeHttpRequests()
    //             .requestMatchers("/login/**", "/oauth2/**", "/logout").permitAll()
    //             .anyRequest().authenticated()
    //         .and()
    //         .oauth2Login(oauth -> oauth
    //             .successHandler(successHandler)
    //         )
    //         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    //         .logout(logout -> logout
    //             .logoutUrl("/logout")
    //             .logoutSuccessHandler((request, response, authentication) -> {
    //                 ResponseCookie clearCookie = ResponseCookie.from("token", "")
    //                         .httpOnly(true)
    //                         .secure(true)
    //                         .sameSite("None")
    //                         .path("/")
    //                         .maxAge(0)
    //                         .build();

    //                 response.setHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
    //                 response.setStatus(HttpServletResponse.SC_OK);
    //             })
    //         );

    //     return http.build();
    // }

     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .cors(Customizer.withDefaults()) // Enable CORS
        .csrf().disable() // (optional) if you're not using CSRF protection
        .authorizeHttpRequests()
            .requestMatchers("/logout", "/login/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
        .and()
        .oauth2Login()
        .defaultSuccessUrl("http://localhost:3000/Start", true)
        .and()
        .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler((request, response, authentication) -> {
                ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build();
                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            });
            return http.build();
    }



    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("https://vite-frontend-gamma.vercel.app")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true) // ✅ Required to send cookies
                    .allowedHeaders("*")
                    .exposedHeaders("Set-Cookie"); // optional
            }
        };
    }



    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.setAllowedOrigins(List.of("https://vite-frontend-gamma.vercel.app"));
    //     config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    //     config.setAllowedHeaders(List.of("*"));
    //     config.setAllowCredentials(true);
    //     config.setExposedHeaders(List.of("Set-Cookie"));

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return source;
    // }



}
