package com.project.demo.model;

import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_number")
    private int apartmentNumber;    
    
    @Column(name = "Bedrooms")
    private int bedrooms;
    
    @Column(name = "Bathrooms")
    private int bathrooms;
    
    @Column(name = "building_id")
    private String buildingId;

    @Column(name = "photo_url")
    private String photoUrl;
}
