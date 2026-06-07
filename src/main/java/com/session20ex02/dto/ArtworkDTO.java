package com.session20ex02.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ArtworkDTO {

    private Long id;

    private String title;

    private String description;

    private boolean published;

    private String ownerUsername;
}
