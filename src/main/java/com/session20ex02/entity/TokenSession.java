package com.session20ex02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token_value", columnDefinition = "TEXT")
    private String refreshTokenValue;

    private boolean revoked;

    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
