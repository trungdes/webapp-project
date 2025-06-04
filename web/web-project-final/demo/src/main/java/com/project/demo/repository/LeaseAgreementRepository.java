package com.project.demo.repository;

import com.project.demo.model.LeaseAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaseAgreementRepository extends JpaRepository<LeaseAgreement, Integer> {
    List<LeaseAgreement> findByApartment_ApartmentNumber(String apartmentNumber);
    List<LeaseAgreement> findByBuilding_BuildingId(String buildingId);
} 