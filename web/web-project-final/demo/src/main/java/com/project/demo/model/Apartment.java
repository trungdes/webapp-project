package com.project.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apartment {
    @Id
    @Column(name = "apartment_number")
    private String apartmentNumber;    
    
    @Column(name = "Bedrooms")
    private Integer bedrooms;
    
    @Column(name = "Bathrooms")
    private Integer bathrooms;
    
    @Column(name = "Price")
    private Double price;
    
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @org.springframework.data.annotation.Transient
    private List<ApartmentPhoto> photos;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Area")
    private Double area;

    @Override
    public String toString() {
        return "Apartment{" +
                "apartmentNumber='" + apartmentNumber + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", price=" + price +
                '}';
    }
}
