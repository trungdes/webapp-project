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

@Controller
public class AdminApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ApartmentPhotoRepository apartmentPhotoRepository;

    @Autowired
    private ViewingScheduleRepository viewingScheduleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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
            @RequestParam("photos") MultipartFile[] photos // nhận nhiều file
    ) {
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
        
        System.out.println("Apartment: " + apartment);
        System.out.println("Apartment.getBuilding(): " + apartment.getBuilding());
        System.out.println("BuildingId: " + buildingId);
        System.out.println("BuildingAddress: " + buildingAddress);
        System.out.println("Type: " + type);
        apartmentService.saveApartment(apartment);

        // Lưu ảnh
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        System.out.println("Upload directory: " + uploadDir);

        for (MultipartFile file : photos) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                try {
                    file.transferTo(dest);
                    System.out.println("Saved file to: " + dest.getAbsolutePath());
                    // Lưu đường dẫn vào DB
                    ApartmentPhoto photo = new ApartmentPhoto();
                    photo.setApartment(apartment);
                    photo.setPhotoUrl("/uploads/" + fileName);  // Thêm dấu / ở đầu
                    System.out.println("Saved photo URL to DB: " + photo.getPhotoUrl());
                    apartmentPhotoRepository.save(photo);
                } catch (IOException e) {
                    System.err.println("Error saving file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<ApartmentDto> apartments = apartmentService.findAllApartments();
        List<ViewingSchedule> viewingSchedules = viewingScheduleRepository.findAll();
        List<Notification> notifications = notificationRepository.findAll();

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
            @RequestParam(value = "photos", required = false) MultipartFile[] photos
    ) {
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

        // (Optional) handle photos

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
} 