package com.project.demo.repository;

import com.project.demo.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, String> {} 