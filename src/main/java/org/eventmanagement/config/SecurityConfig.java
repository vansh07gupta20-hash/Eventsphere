
package org.eventmanagement.config;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.eventmanagement.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:8080")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .authorizeHttpRequests(auth -> auth

                        // Public frontend files
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/pages/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Public authentication endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/admin/auth/login",
                                "/organizer/auth/login"
                        ).permitAll()

                        // Public user registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users"
                        ).permitAll()

                        // Public organizer registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/organizers"
                        ).permitAll()

                        // Password recovery
                        .requestMatchers(
                                "/users/forgot-password",
                                "/users/reset-password"
                        ).permitAll()

                        // Organizer-only event listing
                        // Must come BEFORE the public /events/** rule
                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/organizer"
                        ).hasRole("ORGANIZER")

                        // Public event browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/events",
                                "/events/**"
                        ).permitAll()

                        // Admin-only user status management
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/*/account-status"
                        ).hasRole("ADMIN")

                        // Admin-only user listing and lookup
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users",
                                "/users/",
                                "/users/email/*"
                        ).hasRole("ADMIN")

                        // Other user endpoints
                        .requestMatchers("/users/**").authenticated()

                        // Organizer event creation
                        .requestMatchers(
                                HttpMethod.POST,
                                "/events"
                        ).hasRole("ORGANIZER")

                        // Organizer event updates
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/events/**"
                        ).hasRole("ORGANIZER")

                        // Organizer event deletion
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/events/**"
                        ).hasRole("ORGANIZER")

                        // Organizer-only registration listing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/registrations/organizer"
                        ).hasRole("ORGANIZER")

                        // User event registrations
                        .requestMatchers(
                                HttpMethod.POST,
                                "/registrations/**"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/registrations/my"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/registrations/*/cancel"
                        ).hasRole("USER")

                        // Organizer management: Admin only
                        // POST /organizers is already permitted above
                        .requestMatchers("/organizers/**").hasRole("ADMIN")

                        // Admin management
                        .requestMatchers("/admins/**").hasRole("ADMIN")

                        // Error endpoint
                        .requestMatchers("/error").permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, ex) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
