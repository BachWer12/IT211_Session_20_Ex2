package com.session20ex02.service;

import com.session20ex02.dto.AuthResponse;
import com.session20ex02.dto.LoginRequest;
import com.session20ex02.dto.RefreshTokenRequest;
import com.session20ex02.entity.Account;
import com.session20ex02.entity.TokenSession;
import com.session20ex02.repository.AccountRepository;
import com.session20ex02.repository.TokenSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final TokenSessionRepository tokenSessionRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String accessToken = jwtService.generateAccessToken(account);
        String refreshToken = jwtService.generateRefreshToken(account);

        saveRefreshToken(account, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        TokenSession tokenSession = tokenSessionRepository
                .findByRefreshTokenValue(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (tokenSession.isRevoked() || tokenSession.isExpired()) {
            throw new RuntimeException("Refresh token is revoked or expired");
        }

        Account account = tokenSession.getAccount();

        if (!jwtService.isTokenValid(request.getRefreshToken(), account)) {
            tokenSession.setExpired(true);
            tokenSession.setRevoked(true);
            tokenSessionRepository.save(tokenSession);

            throw new RuntimeException("Refresh token is invalid");
        }

        String newAccessToken = jwtService.generateAccessToken(account);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }

    public void logout(String refreshToken) {
        TokenSession currentSession = tokenSessionRepository
                .findByRefreshTokenValue(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        Account account = currentSession.getAccount();

        var activeSessions = tokenSessionRepository
                .findAllByAccountAndExpiredFalseAndRevokedFalse(account);

        var revokedSessions = activeSessions.stream()
                .peek(session -> {
                    session.setRevoked(true);
                    session.setExpired(true);
                })
                .collect(Collectors.toList());

        tokenSessionRepository.saveAll(revokedSessions);

        SecurityContextHolder.clearContext();
    }

    private void saveRefreshToken(Account account, String refreshTokenValue) {
        TokenSession tokenSession = TokenSession.builder()
                .account(account)
                .refreshTokenValue(refreshTokenValue)
                .revoked(false)
                .expired(false)
                .build();

        tokenSessionRepository.save(tokenSession);
    }
}
