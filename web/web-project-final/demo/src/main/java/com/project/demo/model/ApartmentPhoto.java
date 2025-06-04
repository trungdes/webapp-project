package com.project.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "apartment_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photoUrl;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_number")
    private Apartment apartment;

    @Column(name = "is_cover")
    private Boolean isCover;
} 