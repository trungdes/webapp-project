package com.project.demo.repository;

import com.project.demo.model.ViewingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewingScheduleRepository extends JpaRepository<ViewingSchedule, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ViewingSchedule v WHERE v.apartment.apartmentNumber = :apartmentNumber")
    void deleteByApartmentApartmentNumber(@Param("apartmentNumber") String apartmentNumber);
} 