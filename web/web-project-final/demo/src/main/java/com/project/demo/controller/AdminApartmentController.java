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

@Controller
public class AdminApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ApartmentPhotoRepository apartmentPhotoRepository;

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
        System.out.println("Apartment: " + apartment);
        System.out.println("Apartment.getBuilding(): " + apartment.getBuilding());
        System.out.println("BuildingId: " + buildingId);
        System.out.println("BuildingAddress: " + buildingAddress);
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
        model.addAttribute("apartments", apartmentService.findAllApartments());
        return "admin";
    }
    @PostMapping("/admin/delete-apartment")
    public String deleteApartment(@RequestParam("apartmentNumber") String apartmentNumber) {
        apartmentService.deleteApartmentByNumber(apartmentNumber);
        return "redirect:/admin";
    }
} 