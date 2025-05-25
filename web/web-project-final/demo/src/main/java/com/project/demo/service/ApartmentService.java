package com.project.demo.service;

import java.util.List;

import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.Apartment;

public interface ApartmentService {
    List<ApartmentDto> findAllApartments();

    Apartment saveApartment(Apartment apartment);

    ApartmentDto findApartmentByBuildingId(String buildingId);
}
