
package org.eventmanagement.security;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eventmanagement.entity.Admin;
import org.eventmanagement.entity.User;
import org.eventmanagement.entity.Organizer;

import org.eventmanagement.repository.AdminRepository;
import org.eventmanagement.repository.UserRepository;
import org.eventmanagement.repository.OrganizerRepository;

import org.eventmanagement.service.JwtService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OrganizerRepository organizerRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            AdminRepository adminRepository,
            OrganizerRepository organizerRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.organizerRepository = organizerRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            Claims claims = jwtService.extractClaims(token);

            String role = claims.get("role", String.class);
            String email = claims.getSubject();

            if (email == null ||
                    SecurityContextHolder.getContext().getAuthentication() != null) {

                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = null;

            // USER authentication
            if ("USER".equals(role)) {

                Optional<User> userOptional =
                        userRepository.findByUserEmail(email);

                if (userOptional.isPresent()) {

                    User user = userOptional.get();

                    if ("ACTIVE".equals(user.getUserAccountStatus())) {

                        authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority("ROLE_USER")
                                        )
                                );
                    }
                }
            }

            // ADMIN authentication
            else if ("ADMIN".equals(role)) {

                Optional<Admin> adminOptional =
                        adminRepository.findByAdminEmail(email);

                if (adminOptional.isPresent()) {

                    Admin admin = adminOptional.get();

                    authentication =
                            new UsernamePasswordAuthenticationToken(
                                    admin,
                                    null,
                                    List.of(
                                            new SimpleGrantedAuthority("ROLE_ADMIN")
                                    )
                            );
                }
            }

            // ORGANIZER authentication
            else if ("ORGANIZER".equals(role)) {

                Optional<Organizer> organizerOptional =
                        organizerRepository.findByOrganizerEmail(email);

                if (organizerOptional.isPresent()) {

                    Organizer organizer = organizerOptional.get();

                    if ("ACTIVE".equals(
                            organizer.getOrganizerAccountStatus())) {

                        authentication =
                                new UsernamePasswordAuthenticationToken(
                                        organizer,
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_ORGANIZER"
                                                )
                                        )
                                );
                    }
                }
            }

            // Set authenticated user in SecurityContext
            if (authentication != null) {

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Invalid or expired JWT.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
