package com.project.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ViewingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String notes;
    private String date;
    private String time;

    @ManyToOne
    @JoinColumn(name = "apartment_number")
    private Apartment apartment;
} 