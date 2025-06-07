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
import com.project.demo.model.ViewingSchedule;
import com.project.demo.repository.ViewingScheduleRepository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final ViewingScheduleRepository viewingScheduleRepository;

    public ApartmentController(ApartmentService apartmentService, ViewingScheduleRepository viewingScheduleRepository) {
        this.apartmentService = apartmentService;
        this.viewingScheduleRepository = viewingScheduleRepository;
    }

    @GetMapping("/apartments")
    public String showApartmentsPage(
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String bathrooms,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        List<ApartmentDto> allApartments = apartmentService.findAllApartments().stream()
            .filter(apt -> "SALE".equals(apt.getType()))
            .toList();
        
        if (bedrooms != null && !bedrooms.isEmpty()) {
            allApartments = allApartments.stream()
                .filter(apt -> apt.getBedrooms() == Integer.parseInt(bedrooms))
                .toList();
        }
        
        if (bathrooms != null && !bathrooms.isEmpty()) {
            allApartments = allApartments.stream()
                .filter(apt -> apt.getBathrooms() == Integer.parseInt(bathrooms))
                .toList();
        }
        
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            double minPrice = Double.parseDouble(range[0]);
            double maxPrice = Double.parseDouble(range[1]);
            
            allApartments = allApartments.stream()
                .filter(apt -> apt.getPrice() >= minPrice && apt.getPrice() <= maxPrice)
                .toList();
        }

        // Calculate pagination
        int totalApartments = allApartments.size();
        int totalPages = (int) Math.ceil((double) totalApartments / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalApartments);
        
        List<ApartmentDto> paginatedApartments = allApartments.subList(startIndex, endIndex);
        
        // Create photo map
        Map<String, String> apartmentMainPhotoMap = new HashMap<>();
        for (ApartmentDto apartment : paginatedApartments) {
            List<ApartmentPhoto> photos = apartmentService.findPhotosByApartmentNumber(apartment.getApartmentNumber());
            if (!photos.isEmpty()) {
                apartmentMainPhotoMap.put(apartment.getApartmentNumber(), photos.get(0).getPhotoUrl());
            }
        }
        
        model.addAttribute("apartments", paginatedApartments);
        model.addAttribute("apartmentMainPhotoMap", apartmentMainPhotoMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        List<ApartmentDto> allApartments = apartmentService.findAllApartments().stream()
            .filter(apt -> "RENT".equals(apt.getType()) && (apt.getStatus() == null || !"RENTED".equals(apt.getStatus())))
            .toList();
        
        if (bedrooms != null && !bedrooms.isEmpty()) {
            allApartments = allApartments.stream()
                .filter(apt -> apt.getBedrooms() == Integer.parseInt(bedrooms))
                .toList();
        }
        
        if (bathrooms != null && !bathrooms.isEmpty()) {
            allApartments = allApartments.stream()
                .filter(apt -> apt.getBathrooms() == Integer.parseInt(bathrooms))
                .toList();
        }
        
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            double minPrice = Double.parseDouble(range[0]);
            double maxPrice = Double.parseDouble(range[1]);
            
            allApartments = allApartments.stream()
                .filter(apt -> apt.getPrice() >= minPrice && apt.getPrice() <= maxPrice)
                .toList();
        }

        // Calculate pagination
        int totalApartments = allApartments.size();
        int totalPages = (int) Math.ceil((double) totalApartments / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalApartments);
        
        List<ApartmentDto> paginatedApartments = allApartments.subList(startIndex, endIndex);
        
        // Create photo map
        Map<String, String> apartmentMainPhotoMap = new HashMap<>();
        for (ApartmentDto apartment : paginatedApartments) {
            List<ApartmentPhoto> photos = apartmentService.findPhotosByApartmentNumber(apartment.getApartmentNumber());
            if (!photos.isEmpty()) {
                apartmentMainPhotoMap.put(apartment.getApartmentNumber(), photos.get(0).getPhotoUrl());
            }
        }
        
        model.addAttribute("apartments", paginatedApartments);
        model.addAttribute("apartmentMainPhotoMap", apartmentMainPhotoMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        
        return "lease";
    }
}
