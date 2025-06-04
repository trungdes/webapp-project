package com.project.demo.controller;

import com.project.demo.model.LeaseAgreement;
import com.project.demo.model.Notification;
import com.project.demo.service.LeaseAgreementService;
import com.project.demo.service.ApartmentService;
import com.project.demo.repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/lease-agreements")
public class LeaseAgreementController {
    private final LeaseAgreementService leaseAgreementService;
    private final ApartmentService apartmentService;
    private final NotificationRepository notificationRepository;

    public LeaseAgreementController(LeaseAgreementService leaseAgreementService, 
                                  ApartmentService apartmentService,
                                  NotificationRepository notificationRepository) {
        this.leaseAgreementService = leaseAgreementService;
        this.apartmentService = apartmentService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/create/{apartmentNumber}")
    public String showCreateForm(@PathVariable String apartmentNumber, Model model) {
        model.addAttribute("apartment", apartmentService.findApartmentById(apartmentNumber));
        return "lease-agreement";
    }

    @PostMapping
    public String createLeaseAgreement(@ModelAttribute LeaseAgreement leaseAgreement) {
        leaseAgreement.setStatus("WAITING");
        // Save lease agreement
        LeaseAgreement savedAgreement = leaseAgreementService.saveLeaseAgreement(leaseAgreement);
        
        // Create notification for admin
        Notification notification = Notification.builder()
            .apartment(leaseAgreement.getApartment())
            .message("New lease agreement request for apartment " + leaseAgreement.getApartment().getApartmentNumber() + 
                    " from " + leaseAgreement.getTenantName())
            .isRead(false)
            .createdAt(LocalDateTime.now().toString())
            .build();
        notificationRepository.save(notification);

        return "redirect:/apartments/" + leaseAgreement.getApartment().getApartmentNumber() + "?success=true";
    }

    @GetMapping
    public String listLeaseAgreements(Model model) {
        model.addAttribute("leaseAgreements", leaseAgreementService.getAllLeaseAgreements());
        return "lease-agreements";
    }

    @GetMapping("/{id}")
    public String viewLeaseAgreement(@PathVariable Integer id, Model model) {
        model.addAttribute("leaseAgreement", leaseAgreementService.getLeaseAgreementById(id));
        return "lease-agreement-detail";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteLeaseAgreement(@PathVariable Integer id) {
        leaseAgreementService.deleteLeaseAgreement(id);
    }
} 