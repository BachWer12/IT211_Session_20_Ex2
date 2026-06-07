package com.session20ex02.config;

import com.session20ex02.entity.Account;
import com.session20ex02.repository.TokenSessionRepository;
import com.session20ex02.service.CustomUserDetailsService;
import com.session20ex02.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenSessionRepository tokenSessionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/api/gallery/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);
        String username = jwtService.extractUsername(accessToken);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Account account = (Account) userDetailsService.loadUserByUsername(username);

            boolean hasActiveRefreshSession = tokenSessionRepository
                    .findAllByAccountAndExpiredFalseAndRevokedFalse(account)
                    .stream()
                    .findAny()
                    .isPresent();

            if (jwtService.isTokenValid(accessToken, account) && hasActiveRefreshSession) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                account,
                                null,
                                account.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
