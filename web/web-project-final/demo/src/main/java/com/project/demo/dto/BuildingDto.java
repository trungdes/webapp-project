package com.project.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingDto {
    private String buildingId;
    private String address;
} 