package com.project.demo.controller;

import com.project.demo.model.Apartment;
import com.project.demo.service.ApartmentService;
import com.project.demo.repository.BuildingRepository;
import com.project.demo.model.Building;
import com.project.demo.repository.ApartmentPhotoRepository;
import com.project.demo.model.ApartmentPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import org.springframework.web.bind.WebDataBinder;
import com.project.demo.repository.ViewingScheduleRepository;
import com.project.demo.repository.NotificationRepository;
import java.util.List;
import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.ViewingSchedule;
import com.project.demo.model.Notification;
import com.project.demo.service.LeaseAgreementService;
import com.project.demo.model.LeaseAgreement;

@Controller
public class AdminApartmentController {

    private final ApartmentService apartmentService;
    private final BuildingRepository buildingRepository;
    private final ApartmentPhotoRepository apartmentPhotoRepository;
    private final ViewingScheduleRepository viewingScheduleRepository;
    private final NotificationRepository notificationRepository;
    private final LeaseAgreementService leaseAgreementService;
    private final String uploadDir = "uploads";

    @Autowired
    public AdminApartmentController(ApartmentService apartmentService, BuildingRepository buildingRepository, ApartmentPhotoRepository apartmentPhotoRepository, ViewingScheduleRepository viewingScheduleRepository, NotificationRepository notificationRepository, LeaseAgreementService leaseAgreementService) {
        this.apartmentService = apartmentService;
        this.buildingRepository = buildingRepository;
        this.apartmentPhotoRepository = apartmentPhotoRepository;
        this.viewingScheduleRepository = viewingScheduleRepository;
        this.notificationRepository = notificationRepository;
        this.leaseAgreementService = leaseAgreementService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("photos");
    }

    @GetMapping("/admin/add-apartment")
    public String showAddApartmentForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        return "add-apartment"; // Bạn cần tạo file add-apartment.html hoặc dùng modal trong admin.html
    }

    @PostMapping("/admin/add-apartment")
    public String addApartment(
            @ModelAttribute("apartment") Apartment apartment,
            @RequestParam("building.buildingId") String buildingId,
            @RequestParam(value = "building.address", required = false) String buildingAddress,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam("coverPhoto") MultipartFile coverPhoto,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos
    ) throws IOException {
        Building building = buildingRepository.findById(buildingId).orElse(null);
        if (building == null) {
            building = new Building();
            building.setBuildingId(buildingId);
            building.setAddress(buildingAddress != null ? buildingAddress : "Unknown");
            buildingRepository.save(building);
        }
        apartment.setBuilding(building);
        // Set default type if not provided
        if (type == null || type.isEmpty()) {
            type = "SALE";
        }
        apartment.setType(type);
        apartmentService.saveApartment(apartment);
        String uploadDir = System.getProperty("user.dir") + File.separator + this.uploadDir + File.separator;
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        // Lưu ảnh bìa
        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + coverPhoto.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            coverPhoto.transferTo(dest);
            ApartmentPhoto photo = new ApartmentPhoto();
            photo.setApartment(apartment);
            photo.setPhotoUrl("/uploads/" + fileName);
            photo.setIsCover(true);
            apartmentPhotoRepository.save(photo);
        }
        // Lưu các ảnh khác
        if (photos != null) {
            for (MultipartFile file : photos) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    File dest = new File(uploadDir + fileName);
                    file.transferTo(dest);
                    ApartmentPhoto photo = new ApartmentPhoto();
                    photo.setApartment(apartment);
                    photo.setPhotoUrl("/uploads/" + fileName);
                    photo.setIsCover(false);
                    apartmentPhotoRepository.save(photo);
                }
            }
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<ApartmentDto> apartments = apartmentService.findAllApartments();
        List<ViewingSchedule> viewingSchedules = viewingScheduleRepository.findAll();
        List<Notification> notifications = notificationRepository.findByTenantEmailIsNull();
        List<LeaseAgreement> leases = leaseAgreementService.getAllLeaseAgreements();

        // Số căn hộ
        int totalApartments = apartments.size();
        // Số căn hộ còn trống (ví dụ: căn hộ bán)
        int availableApartments = (int) apartments.stream().filter(a -> a.getType() != null && a.getType().equals("SALE")).count();
        // Số lịch hẹn
        int totalViewings = viewingSchedules.size();
        // Số notification chưa đọc
        int newNotifications = (int) notifications.stream().filter(n -> !n.isRead()).count();

        model.addAttribute("apartments", apartments);
        model.addAttribute("viewingSchedules", viewingSchedules);
        model.addAttribute("notifications", notifications);
        model.addAttribute("totalApartments", totalApartments);
        model.addAttribute("availableApartments", availableApartments);
        model.addAttribute("totalViewings", totalViewings);
        model.addAttribute("newNotifications", newNotifications);
        model.addAttribute("leases", leases);
        return "admin";
    }
    @PostMapping("/admin/delete-apartment")
    public String deleteApartment(@RequestParam("apartmentNumber") String apartmentNumber) {
        apartmentService.deleteApartmentByNumber(apartmentNumber);
        return "redirect:/admin#apartments";
    }

    @PostMapping("/admin/edit-apartment")
    public String editApartment(
            @ModelAttribute Apartment formApartment,
            @RequestParam("building.buildingId") String buildingId,
            @RequestParam(value = "building.address", required = false) String buildingAddress,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos,
            @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto,
            @RequestParam(value = "coverPhotoId", required = false) Long coverPhotoId
    ) throws IOException {
        Apartment existing = apartmentService.findById(formApartment.getApartmentNumber());
        if (existing == null) {
            // handle not found
            return "redirect:/admin?error=notfound";
        }
        // Only update fields that are not null or not empty
        if (formApartment.getBedrooms() != null) existing.setBedrooms(formApartment.getBedrooms());
        if (formApartment.getBathrooms() != null) existing.setBathrooms(formApartment.getBathrooms());
        if (formApartment.getPrice() != null) existing.setPrice(formApartment.getPrice());
        if (formApartment.getArea() != null) existing.setArea(formApartment.getArea());
        if (formApartment.getDescription() != null && !formApartment.getDescription().isEmpty())
            existing.setDescription(formApartment.getDescription());
        if (type != null && !type.isEmpty()) existing.setType(type);
        // Building logic
        Building building = buildingRepository.findById(buildingId).orElse(existing.getBuilding());
        if (building == null) {
            building = new Building();
            building.setBuildingId(buildingId);
            building.setAddress(buildingAddress != null ? buildingAddress : "Unknown");
            buildingRepository.save(building);
        }
        existing.setBuilding(building);
        String uploadDir = System.getProperty("user.dir") + File.separator + this.uploadDir + File.separator;
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        // Lưu ảnh bìa mới nếu có
        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            // Đặt tất cả ảnh hiện tại về isCover=false và xóa file vật lý của ảnh bìa cũ
            List<ApartmentPhoto> allPhotos = apartmentPhotoRepository.findByApartmentApartmentNumber(existing.getApartmentNumber());
            for (ApartmentPhoto photo : allPhotos) {
                if (Boolean.TRUE.equals(photo.getIsCover()) && photo.getPhotoUrl() != null && !photo.getPhotoUrl().isEmpty()) {
                    String fileName = photo.getPhotoUrl().startsWith("/uploads/") ? photo.getPhotoUrl().substring("/uploads/".length()) : photo.getPhotoUrl();
                    String filePath = System.getProperty("user.dir") + File.separator + this.uploadDir + File.separator + fileName;
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                photo.setIsCover(false);
                apartmentPhotoRepository.save(photo);
            }
            String fileName = System.currentTimeMillis() + "_" + coverPhoto.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            coverPhoto.transferTo(dest);
            ApartmentPhoto newCover = new ApartmentPhoto();
            newCover.setApartment(existing);
            newCover.setPhotoUrl("/uploads/" + fileName);
            newCover.setIsCover(true);
            apartmentPhotoRepository.save(newCover);
        }
        // Lưu các ảnh khác nếu có
        if (photos != null) {
            for (MultipartFile file : photos) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    File dest = new File(uploadDir + fileName);
                    file.transferTo(dest);
                    ApartmentPhoto photo = new ApartmentPhoto();
                    photo.setApartment(existing);
                    photo.setPhotoUrl("/uploads/" + fileName);
                    photo.setIsCover(false);
                    apartmentPhotoRepository.save(photo);
                }
            }
        }
        // Nếu chọn coverPhotoId từ radio cũ thì vẫn giữ logic cũ
        if (coverPhotoId != null) {
            ApartmentPhoto coverPhotoEntity = apartmentPhotoRepository.findById(coverPhotoId).orElse(null);
            if (coverPhotoEntity != null) {
                List<ApartmentPhoto> allPhotos = apartmentPhotoRepository.findByApartmentApartmentNumber(existing.getApartmentNumber());
                for (ApartmentPhoto photo : allPhotos) {
                    photo.setIsCover(false);
                    apartmentPhotoRepository.save(photo);
                }
                coverPhotoEntity.setIsCover(true);
                apartmentPhotoRepository.save(coverPhotoEntity);
            }
        }
        apartmentService.saveApartment(existing);
        return "redirect:/admin#apartments";
    }

    @PostMapping("/admin/delete-viewing-schedule")
    public String deleteViewingSchedule(@RequestParam("id") Long id) {
        viewingScheduleRepository.deleteById(id);
        return "redirect:/admin#viewings";
    }

    @PostMapping("/admin/mark-notification-read")
    public String markNotificationRead(@RequestParam("id") Long id) {
        Notification noti = notificationRepository.findById(id).orElse(null);
        if (noti != null) {
            noti.setRead(true);
            notificationRepository.save(noti);
        }
        return "redirect:/admin#notifications";
    }

    @PostMapping("/admin/mark-all-notifications-read")
    public String markAllNotificationsRead() {
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification noti : notifications) {
            if (!noti.isRead()) {
                noti.setRead(true);
                notificationRepository.save(noti);
            }
        }
        return "redirect:/admin#notifications";
    }

    @GetMapping("/api/building-address")
    @ResponseBody
    public String getBuildingAddress(@RequestParam("buildingId") String buildingId) {
        Building building = buildingRepository.findById(buildingId).orElse(null);
        return building != null ? building.getAddress() : "";
    }

    @PostMapping("/admin/lease-accept")
    public String acceptLease(@RequestParam("leaseId") Integer leaseId) {
        LeaseAgreement lease = leaseAgreementService.getLeaseAgreementById(leaseId);
        if (lease != null) {
            // Cập nhật trạng thái hợp đồng
            lease.setStatus("ACTIVE");
            leaseAgreementService.saveLeaseAgreement(lease);

            // Cập nhật trạng thái căn hộ
            Apartment apartment = lease.getApartment();
            apartment.setStatus("RENTED");
            apartment.setCurrentTenantEmail(lease.getTenantEmail());
            apartmentService.saveApartment(apartment);

            // Tạo notification cho user
            Notification notification = Notification.builder()
                .message("Hợp đồng thuê của bạn đã được duyệt!")
                .isRead(false)
                .createdAt(java.time.LocalDateTime.now().toString())
                .tenantEmail(lease.getTenantEmail())
                .build();
            notificationRepository.save(notification);
        }
        return "redirect:/admin#leases";
    }

    @PostMapping("/admin/lease-reject")
    public String rejectLease(@RequestParam("leaseId") Integer leaseId) {
        LeaseAgreement lease = leaseAgreementService.getLeaseAgreementById(leaseId);
        if (lease != null) {
            // Cập nhật trạng thái hợp đồng
            lease.setStatus("REJECTED");
            leaseAgreementService.saveLeaseAgreement(lease);

            // Cập nhật trạng thái căn hộ
            Apartment apartment = lease.getApartment();
            apartment.setStatus("AVAILABLE");
            apartment.setCurrentTenantEmail(null);
            apartmentService.saveApartment(apartment);

            // Tạo notification cho user
            Notification notification = Notification.builder()
                .message("Hợp đồng thuê của bạn đã bị từ chối.")
                .isRead(false)
                .createdAt(java.time.LocalDateTime.now().toString())
                .tenantEmail(lease.getTenantEmail())
                .build();
            notificationRepository.save(notification);
        }
        return "redirect:/admin#leases";
    }

    @PostMapping("/admin/lease-delete")
    public String deleteLease(@RequestParam("leaseId") Integer leaseId) {
        leaseAgreementService.deleteLeaseAgreementById(leaseId);
        return "redirect:/admin#leases";
    }
} 