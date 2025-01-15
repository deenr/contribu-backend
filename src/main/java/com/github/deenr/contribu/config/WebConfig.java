package com.github.deenr.contribu.config;

import com.github.deenr.contribu.controller.UserController;
import com.github.deenr.contribu.security.JwtAuthenticationFilter;
import com.github.deenr.contribu.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("POST", "PUT", "PATCH", "GET", "OPTIONS", "DELETE"));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Accept",
                "X-Requested-With",
                "Content-Type",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));

        config.setExposedHeaders(List.of(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"));

        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager, JwtService.TOKEN_KEY);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .logout((logout) -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            Cookie cookie = new Cookie(UserController.REFRESH_COOKIE, "");
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            cookie.setMaxAge((int) (JwtService.REFRESH_TOKEN_EXPIRATION / 1000));
                            cookie.setPath("/");

                            response.addCookie(cookie);
                        })
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());

        return http.build();
    }
}