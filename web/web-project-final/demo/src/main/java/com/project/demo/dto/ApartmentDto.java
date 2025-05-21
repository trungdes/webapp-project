package com.project.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApartmentDto {
    private int apartmentNumber;
    private int bedrooms;
    private int bathrooms;
    private String photoUrl;
}
