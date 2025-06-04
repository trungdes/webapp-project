package com.project.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.project.demo.model.ApartmentPhoto;

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
    private String type;
    private List<ApartmentPhoto> photos;
    private String status;
}
