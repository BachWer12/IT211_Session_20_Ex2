package com.session20ex02.config;

import com.session20ex02.entity.Account;
import com.session20ex02.entity.Artwork;
import com.session20ex02.entity.Role;
import com.session20ex02.repository.AccountRepository;
import com.session20ex02.repository.ArtworkRepository;
import com.session20ex02.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final ArtworkRepository artworkRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;
        }

        Role adminRole = Role.builder()
                .roleName("ROLE_ADMIN")
                .build();

        Role artistRole = Role.builder()
                .roleName("ROLE_ARTIST")
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(artistRole);

        Account admin = Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .active(true)
                .roles(Set.of(adminRole))
                .build();

        Account artistA = Account.builder()
                .username("artist_a")
                .password(passwordEncoder.encode("123456"))
                .active(true)
                .roles(Set.of(artistRole))
                .build();

        Account artistB = Account.builder()
                .username("artist_b")
                .password(passwordEncoder.encode("123456"))
                .active(true)
                .roles(Set.of(artistRole))
                .build();

        accountRepository.save(admin);
        accountRepository.save(artistA);
        accountRepository.save(artistB);

        Artwork artwork1 = Artwork.builder()
                .title("Sunset Dream")
                .description("A public artwork by artist A")
                .published(true)
                .owner(artistA)
                .build();

        Artwork artwork2 = Artwork.builder()
                .title("Hidden Ocean")
                .description("An unpublished artwork by artist A")
                .published(false)
                .owner(artistA)
                .build();

        Artwork artwork3 = Artwork.builder()
                .title("Ancient River")
                .description("A public artwork by artist B")
                .published(true)
                .owner(artistB)
                .build();

        Artwork artwork4 = Artwork.builder()
                .title("Secret Mountain")
                .description("An unpublished artwork by artist B")
                .published(false)
                .owner(artistB)
                .build();

        artworkRepository.saveAll(
                Set.of(
                        artwork1,
                        artwork2,
                        artwork3,
                        artwork4
                )
        );
    }
}
