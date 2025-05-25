package com.project.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
=======
>>>>>>> cuong

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
    
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(name = "photo_url")
    private String photoUrl;
}
