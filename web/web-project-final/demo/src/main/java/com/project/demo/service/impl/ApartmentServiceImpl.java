package com.project.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.demo.dto.ApartmentDto;
import com.project.demo.dto.BuildingDto;
import com.project.demo.model.Apartment;
import com.project.demo.model.ApartmentPhoto;
import com.project.demo.repository.ApartmentRepository;
import com.project.demo.repository.ApartmentPhotoRepository;
import com.project.demo.service.ApartmentService;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private ApartmentRepository apartmentRepository;
    private ApartmentPhotoRepository apartmentPhotoRepository;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, ApartmentPhotoRepository apartmentPhotoRepository) {
        this.apartmentRepository = apartmentRepository;
        this.apartmentPhotoRepository = apartmentPhotoRepository;
    }

    @Override
    public List<ApartmentDto> findAllApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        return apartments.stream().map(apartment -> mapToDto(apartment)).collect(Collectors.toList());
    }

    private ApartmentDto mapToDto(Apartment apartment) {
        BuildingDto buildingDto = null;
        if (apartment.getBuilding() != null) {
            buildingDto = BuildingDto.builder()
                .buildingId(apartment.getBuilding().getBuildingId())
                .address(apartment.getBuilding().getAddress())
                .build();
        }

        return ApartmentDto.builder()
            .apartmentNumber(apartment.getApartmentNumber())
            .bedrooms(apartment.getBedrooms())
            .bathrooms(apartment.getBathrooms())
            .building(buildingDto)
            .price(apartment.getPrice())
            .build();
    }

    @Override
    public Apartment saveApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    @Override
    public List<ApartmentDto> findApartmentByBuildingId(String buildingId) {
        List<Apartment> apartments = apartmentRepository.findByBuildingBuildingId(buildingId);
        return apartments.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ApartmentPhoto> findPhotosByApartmentNumber(String apartmentNumber) {
        return apartmentPhotoRepository.findPhotosByApartmentNumber(apartmentNumber);
    }

    @Override
    public ApartmentDto findApartmentById(String id) {
        System.out.println("Finding apartment with id: " + id);
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        if (apartment == null) {
            System.err.println("No apartment found with id: " + id);
            return null;
        }
        System.out.println("Found apartment: " + apartment);
        return mapToDto(apartment);
    }

    @Override
    public ApartmentDto findApartmentByNumber(String apartmentNumber) {
        return findApartmentById(apartmentNumber);
    }

    @Override
    public void deleteApartmentByNumber(String apartmentNumber) {
        apartmentRepository.deleteById(apartmentNumber);
    }
        
}
