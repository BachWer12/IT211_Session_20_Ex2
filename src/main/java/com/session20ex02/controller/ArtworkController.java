package com.session20ex02.controller;

import com.session20ex02.dto.ArtworkDTO;
import com.session20ex02.service.ArtworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getArtworks() {
        return ResponseEntity.ok(artworkService.getArtworksForCurrentUser());
    }
}
