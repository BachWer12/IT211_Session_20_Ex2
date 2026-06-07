package com.session20ex02.service;

import com.session20ex02.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}
