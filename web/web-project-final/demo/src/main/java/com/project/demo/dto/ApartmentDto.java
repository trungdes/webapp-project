package com.project.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {
    private String apartmentNumber;
    private int bedrooms;
    private int bathrooms;
    private BuildingDto building;
    private double price;
    private String description;
    private double area;
}
