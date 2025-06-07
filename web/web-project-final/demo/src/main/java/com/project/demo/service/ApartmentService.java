package com.project.demo.service;

import java.util.List;

import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.Apartment;
import com.project.demo.model.ApartmentPhoto;

public interface ApartmentService {
    List<ApartmentDto> findAllApartments();

    Apartment saveApartment(Apartment apartment);

    List<ApartmentDto> findApartmentByBuildingId(String buildingId);

    List<ApartmentPhoto> findPhotosByApartmentNumber(String apartmentNumber);

    ApartmentDto findApartmentById(String id);

    ApartmentDto findApartmentByNumber(String apartmentNumber);

    void deleteApartmentByNumber(String apartmentNumber);

    Apartment findById(String apartmentNumber);

    void updateApartmentStatus(String apartmentNumber, String status);
}
