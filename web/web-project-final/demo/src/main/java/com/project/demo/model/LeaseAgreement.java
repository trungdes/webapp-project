package com.project.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "leaseagreement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaseAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LeaseID")
    private Integer leaseId;

    @ManyToOne
    @JoinColumn(name = "ApartmentNumber")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "BuildingID")
    private Building building;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "SecurityDeposit")
    private Double securityDeposit;

    @Column(name = "MonthlyRent")
    private Double monthlyRent;

    @Column(name = "ManagerSSN")
    private String managerSSN;

    @Column(name = "TenantName")
    private String tenantName;

    @Column(name = "TenantPhone")
    private String tenantPhone;

    @Column(name = "TenantEmail")
    private String tenantEmail;

    @Column(name = "TenantId")
    private String tenantId;

    @Column(name = "Status")
    private String status; // WAITING, ACTIVE, REJECTED

    // Thêm các trường khác nếu cần
} 