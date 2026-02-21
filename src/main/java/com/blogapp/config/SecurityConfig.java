package com.blogapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${blog.admin.username:admin}")
    private String adminUsername;

    @Value("${blog.admin.password:admin123}")
    private String adminPassword;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints - blogs, reactions, comments
                        .requestMatchers(HttpMethod.GET, "/api/blogs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/blogs/*/reaction").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blogs/*/reaction").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blogs/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/blogs/*/comments").permitAll()

                        // Public endpoints - sections & subsections
                        .requestMatchers(HttpMethod.GET, "/api/sections/**").permitAll()

                        // Public endpoints - auth (user login/register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public endpoints - subscription
                        .requestMatchers("/api/blogs/subscribe/**").permitAll()

                        // Public endpoints - submission
                        .requestMatchers("/api/blogs/submission/**").permitAll()

                        // Public endpoints - pricing
                        .requestMatchers(HttpMethod.GET, "/api/pricing/**").permitAll()

                        // Swagger / API docs - public
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Actuator - public
                        .requestMatchers("/actuator/**").permitAll()

                        // User-authenticated endpoints (JWT)
                        .requestMatchers("/api/account/**").authenticated()
                        .requestMatchers("/api/checkout/**").authenticated()

                        // Admin endpoints - require ADMIN role (HTTP Basic)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests - authenticated
                        .anyRequest().authenticated())
                // JWT filter runs first — sets user auth from Bearer token
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // HTTP Basic remains as fallback for admin
                .httpBasic(basic -> {
                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
