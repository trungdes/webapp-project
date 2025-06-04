package com.project.demo.service;

import com.project.demo.model.LeaseAgreement;
import com.project.demo.repository.LeaseAgreementRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaseAgreementService {
    private final LeaseAgreementRepository leaseAgreementRepository;

    public LeaseAgreementService(LeaseAgreementRepository leaseAgreementRepository) {
        this.leaseAgreementRepository = leaseAgreementRepository;
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
        leaseAgreementRepository.deleteById(id);
    }

    public void deleteLeaseAgreementById(Integer id) {
        leaseAgreementRepository.deleteById(id);
    }
} 