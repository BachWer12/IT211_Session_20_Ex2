package com.session20ex02.service;

import com.session20ex02.dto.ArtworkDTO;
import com.session20ex02.entity.Account;
import com.session20ex02.entity.Artwork;
import com.session20ex02.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    public List<ArtworkDTO> getArtworksForCurrentUser() {
        Account currentAccount = (Account) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        var authorities = currentAccount.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        boolean isAdmin = authorities.contains("ROLE_ADMIN");
        boolean isArtist = authorities.contains("ROLE_ARTIST");

        return artworkRepository.findAll()
                .stream()
                .filter(artwork -> {
                    if (isAdmin) {
                        return true;
                    }

                    if (isArtist) {
                        return artwork.isPublished()
                                || artwork.getOwner().getId().equals(currentAccount.getId());
                    }

                    return artwork.isPublished();
                })
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ArtworkDTO toDTO(Artwork artwork) {
        return ArtworkDTO.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .published(artwork.isPublished())
                .ownerUsername(artwork.getOwner().getUsername())
                .build();
    }
}
