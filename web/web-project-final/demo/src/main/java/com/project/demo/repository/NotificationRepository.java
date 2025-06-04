package com.project.demo.repository;

import com.project.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.apartment.apartmentNumber = :apartmentNumber")
    void deleteByApartmentApartmentNumber(@Param("apartmentNumber") String apartmentNumber);

    List<Notification> findByTenantEmailOrderByCreatedAtDesc(String tenantEmail);
    List<Notification> findByTenantEmailAndIsReadFalse(String tenantEmail);
    List<Notification> findByTenantEmailIsNull();
} 