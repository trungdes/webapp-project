package com.project.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.Apartment;
import com.project.demo.repository.ApartmentRepository;
import com.project.demo.service.ApartmentService;
@Service
public class ApartmentServiceImpl implements ApartmentService {
    private ApartmentRepository apartmentRepository;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public List<ApartmentDto> findAllApartments() {
        // TODO Auto-generated method stub
        List<Apartment> apartments = apartmentRepository.findAll();
        return apartments.stream().map(apartment -> mapToDto(apartment)).collect(Collectors.toList());
    }

    private ApartmentDto mapToDto(Apartment apartment) {
        ApartmentDto apartmentDto = ApartmentDto.builder()
        .apartmentNumber(apartment.getApartmentNumber())
        .bedrooms(apartment.getBedrooms())
        .bathrooms(apartment.getBathrooms())
        .photoUrl(apartment.getPhotoUrl())
        .build();
        return apartmentDto;    
        
    }
}
