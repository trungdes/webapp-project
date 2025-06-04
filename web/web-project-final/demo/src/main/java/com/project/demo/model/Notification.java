package com.project.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    @Column(name = "is_read")
    private boolean isRead;
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "apartment_number")
    private Apartment apartment;
} 