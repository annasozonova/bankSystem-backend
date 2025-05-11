package com.annasozonova.bank.config;

import com.annasozonova.bank.security.CustomUserDetailsService;
import com.annasozonova.bank.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security configuration for JWT-based stateless authentication and role-based authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = customUserDetailsService;
    }

    /**
     * Defines security rules for HTTP requests, including JWT authentication and role access.
     *
     * @param http HTTP security configuration
     * @return configured security filter chain
     * @throws Exception in case of misconfiguration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // OpenAPI & Swagger UI
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-ui/**",
                                "/webjars/**"
                        ).permitAll()

                        // Cards: GET allowed for both USER and ADMIN; others — ADMIN only
                        .requestMatchers(HttpMethod.GET, "/cards/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/cards/**").hasRole("ADMIN")

                        // User management: ADMIN only
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Custom UserDetailsService
                .userDetailsService(userDetailsService)
                // Insert JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration allowing cross-origin requests from frontend.
     *
     * @return configured CORS source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:8080"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Exposes the {@link AuthenticationManager} used by Spring Security.
     *
     * @param configuration injected auth configuration
     * @return authentication manager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Password encoder bean using BCrypt hashing.
     *
     * @return password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
