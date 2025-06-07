package com.project.demo.service;

import com.project.demo.model.LeaseAgreement;
import com.project.demo.repository.LeaseAgreementRepository;
import com.project.demo.service.ApartmentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaseAgreementService {
    private final LeaseAgreementRepository leaseAgreementRepository;
    private final ApartmentService apartmentService;

    public LeaseAgreementService(LeaseAgreementRepository leaseAgreementRepository, ApartmentService apartmentService) {
        this.leaseAgreementRepository = leaseAgreementRepository;
        this.apartmentService = apartmentService;
    }

    public LeaseAgreement saveLeaseAgreement(LeaseAgreement leaseAgreement) {
        return leaseAgreementRepository.save(leaseAgreement);
    }

    public List<LeaseAgreement> getAllLeaseAgreements() {
        return leaseAgreementRepository.findAll();
    }

    public LeaseAgreement getLeaseAgreementById(Integer id) {
        return leaseAgreementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lease agreement not found"));
    }

    public void deleteLeaseAgreement(Integer id) {
        LeaseAgreement lease = getLeaseAgreementById(id);
        if (lease != null && lease.getApartment() != null) {
            // Update apartment status to AVAILABLE
            apartmentService.updateApartmentStatus(lease.getApartment().getApartmentNumber(), "AVAILABLE");
            // Clear tenant email
            lease.getApartment().setCurrentTenantEmail(null);
            apartmentService.saveApartment(lease.getApartment());
        }
        leaseAgreementRepository.deleteById(id);
    }

    public void deleteLeaseAgreementById(Integer id) {
        deleteLeaseAgreement(id);
    }
} 