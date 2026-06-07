package com.session20ex02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean published;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Account owner;
}