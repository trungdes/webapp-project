package com.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.demo.model.Apartment;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    
}
