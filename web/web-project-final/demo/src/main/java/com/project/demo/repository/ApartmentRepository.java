package com.project.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.demo.model.Apartment;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    Optional<Apartment> findByBuildingBuildingId(String buildingId);
}
