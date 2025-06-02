package com.project.demo.repository;

import com.project.demo.model.ApartmentPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApartmentPhotoRepository extends JpaRepository<ApartmentPhoto, Long> {
    List<ApartmentPhoto> findByApartmentApartmentNumber(String apartmentNumber);

    @Query("SELECT p FROM ApartmentPhoto p WHERE p.apartment.apartmentNumber = :apartmentNumber")
    List<ApartmentPhoto> findPhotosByApartmentNumber(@Param("apartmentNumber") String apartmentNumber);
} 