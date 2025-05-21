package com.project.demo.service;

import java.util.List;

import com.project.demo.dto.ApartmentDto;

public interface ApartmentService {
    List<ApartmentDto> findAllApartments();
}
