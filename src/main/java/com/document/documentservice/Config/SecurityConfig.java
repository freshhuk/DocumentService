package com.document.documentservice.Config;

import com.document.documentservice.Security.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Конфигурируем HttpSecurity
        return http
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF (так как у нас будет Stateless)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/reg").permitAll()
                        .requestMatchers("/auth/log").permitAll()
                        .requestMatchers("/auth/check-auth").permitAll()
                        .requestMatchers("/AuthorizePage.html").permitAll()
                        .requestMatchers("/index.html").authenticated()
                        .requestMatchers("/api/**").authenticated()//end points with auth
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Отключаем сессии
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Добавляем JWT фильтр в цепочку
                .build();
    }
}
