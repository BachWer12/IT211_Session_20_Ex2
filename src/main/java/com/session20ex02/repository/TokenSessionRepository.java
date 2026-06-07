package com.session20ex02.repository;

import com.session20ex02.entity.Account;
import com.session20ex02.entity.TokenSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenSessionRepository extends JpaRepository<TokenSession, Long> {

    Optional<TokenSession> findByRefreshTokenValue(String refreshTokenValue);

    List<TokenSession> findAllByAccountAndExpiredFalseAndRevokedFalse(Account account);
}
