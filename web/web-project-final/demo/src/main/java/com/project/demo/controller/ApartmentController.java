package com.project.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.demo.service.ApartmentService;
import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.Apartment;
import com.project.demo.model.ApartmentPhoto;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/apartments")
    public String showApartmentsPage(
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String bathrooms,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String location,
            Model model) {
        
        List<ApartmentDto> apartments = apartmentService.findAllApartments().stream()
            .filter(apt -> "SALE".equals(apt.getType()))
            .toList();
        
        if (bedrooms != null && !bedrooms.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBedrooms() == Integer.parseInt(bedrooms))
                .toList();
        }
        
        if (bathrooms != null && !bathrooms.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBathrooms() == Integer.parseInt(bathrooms))
                .toList();
        }
        
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            double minPrice = Double.parseDouble(range[0]);
            double maxPrice = Double.parseDouble(range[1]);
            
            apartments = apartments.stream()
                .filter(apt -> apt.getPrice() >= minPrice && apt.getPrice() <= maxPrice)
                .toList();
        }
        
        if (location != null && !location.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBuilding() != null && 
                       apt.getBuilding().getAddress().toLowerCase().contains(location.toLowerCase()))
                .toList();
        }
        
        Map<String, String> apartmentMainPhotoMap = new HashMap<>();
        for (ApartmentDto apt : apartments) {
            List<ApartmentPhoto> photos = apartmentService.findPhotosByApartmentNumber(apt.getApartmentNumber());
            String mainPhotoUrl = "/css/image.png";
            for (ApartmentPhoto p : photos) {
                if (Boolean.TRUE.equals(p.getIsCover())) {
                    mainPhotoUrl = p.getPhotoUrl();
                    break;
                }
            }
            // Nếu không có ảnh bìa, lấy ảnh đầu tiên nếu có
            if (mainPhotoUrl.equals("/css/image.png") && photos != null && !photos.isEmpty()) {
                mainPhotoUrl = photos.get(0).getPhotoUrl();
            }
            apartmentMainPhotoMap.put(apt.getApartmentNumber(), mainPhotoUrl);
        }
        model.addAttribute("apartments", apartments);
        model.addAttribute("apartmentMainPhotoMap", apartmentMainPhotoMap);
        return "apartments";
    }

    @GetMapping("/api/apartments")
    @ResponseBody
    public List<ApartmentDto> getApartmentsApi() {
        return apartmentService.findAllApartments();
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabaseConnection() {
        try {
            var apartments = apartmentService.findAllApartments();
            return "Database connection successful! Found " + apartments.size() + " apartments.";
        } catch (Exception e) {
            return "Database connection failed! Error: " + e.getMessage();
        }
    }

    @GetMapping("/sell")
    public String createApartmentForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        return "sell";
    }

    @PostMapping("/new")
    public String saveApartment(@ModelAttribute("apartment") Apartment apartment) {
        apartmentService.saveApartment(apartment);
        return "redirect:/sell";
    }

    @GetMapping("/apartments/{id}")
    public String apartmentDetail(@PathVariable("id") String id, Model model) {
        System.out.println("Received apartment id: " + id);
        ApartmentDto apartment = apartmentService.findApartmentById(id);
        if (apartment == null) {
            System.err.println("Apartment not found with id: " + id);
            return "redirect:/apartments";
        }
        System.out.println("Found apartment: " + apartment);
        model.addAttribute("apartment", apartment);
        // Get photos for the apartment
        List<ApartmentPhoto> photos = apartmentService.findPhotosByApartmentNumber(apartment.getApartmentNumber());
        ApartmentPhoto coverPhoto = null;
        List<ApartmentPhoto> otherPhotos = new java.util.ArrayList<>();
        for (ApartmentPhoto p : photos) {
            if (Boolean.TRUE.equals(p.getIsCover())) {
                coverPhoto = p;
            } else {
                otherPhotos.add(p);
            }
        }
        model.addAttribute("coverPhoto", coverPhoto);
        model.addAttribute("photos", otherPhotos);
        return "apartment-detail";
    }

    @GetMapping("/lease")
    public String showLeasePage(
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String bathrooms,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String location,
            Model model) {
        
        List<ApartmentDto> apartments = apartmentService.findAllApartments().stream()
            .filter(apt -> "RENT".equals(apt.getType()))
            .toList();
        
        if (bedrooms != null && !bedrooms.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBedrooms() == Integer.parseInt(bedrooms))
                .toList();
        }
        
        if (bathrooms != null && !bathrooms.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBathrooms() == Integer.parseInt(bathrooms))
                .toList();
        }
        
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            double minPrice = Double.parseDouble(range[0]);
            double maxPrice = Double.parseDouble(range[1]);
            
            apartments = apartments.stream()
                .filter(apt -> apt.getPrice() >= minPrice && apt.getPrice() <= maxPrice)
                .toList();
        }
        
        if (location != null && !location.isEmpty()) {
            apartments = apartments.stream()
                .filter(apt -> apt.getBuilding() != null && 
                       apt.getBuilding().getAddress().toLowerCase().contains(location.toLowerCase()))
                .toList();
        }
        
        Map<String, String> apartmentMainPhotoMap = new HashMap<>();
        for (ApartmentDto apt : apartments) {
            List<ApartmentPhoto> photos = apartmentService.findPhotosByApartmentNumber(apt.getApartmentNumber());
            String mainPhotoUrl = "/css/image.png";
            for (ApartmentPhoto p : photos) {
                if (Boolean.TRUE.equals(p.getIsCover())) {
                    mainPhotoUrl = p.getPhotoUrl();
                    break;
                }
            }
            // Nếu không có ảnh bìa, lấy ảnh đầu tiên nếu có
            if (mainPhotoUrl.equals("/css/image.png") && photos != null && !photos.isEmpty()) {
                mainPhotoUrl = photos.get(0).getPhotoUrl();
            }
            apartmentMainPhotoMap.put(apt.getApartmentNumber(), mainPhotoUrl);
        }
        model.addAttribute("apartments", apartments);
        model.addAttribute("apartmentMainPhotoMap", apartmentMainPhotoMap);
        return "lease";
    }
}
