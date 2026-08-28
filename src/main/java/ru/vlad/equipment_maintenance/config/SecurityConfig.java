package ru.vlad.equipment_maintenance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.vlad.equipment_maintenance.security.JwtTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Полностью разрешаем доступ к фронтенду (страницам, стилям и скриптам)
                .requestMatchers("/", "/index.html", "/style.css", "/app.js").permitAll()
                
                // Эндпоинты авторизации
                .requestMatchers("/auth/**").permitAll()
                
                // Защищенные эндпоинты бизнес-логики
                .requestMatchers("/api/equipment/pretty").permitAll()
                .requestMatchers("/api/equipment/high-risk").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/equipment/stats").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/equipment/schedule").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                
                // Все остальные запросы требуют авторизации
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}