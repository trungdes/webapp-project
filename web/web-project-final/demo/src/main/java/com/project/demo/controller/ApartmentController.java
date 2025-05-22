package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.demo.service.ApartmentService;
import com.project.demo.dto.ApartmentDto;
import com.project.demo.model.Apartment;
import java.util.List;

@Controller
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;
    
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }
    
    @GetMapping("/apartments")
    public String listApartments(Model model) {
        List<ApartmentDto> apartments = apartmentService.findAllApartments();
        System.out.println("Apartments: " + apartments);
        model.addAttribute("apartments", apartments);
        return "apartments";
    }
    @GetMapping("/api/apartments")
    @ResponseBody
    public List<ApartmentDto> listApartments() {
        return apartmentService.findAllApartments();
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabase() {
        try {
            var apartments = apartmentService.findAllApartments();
            return "Database connection successful! Found " + apartments.size() + " apartments.";
        } catch (Exception e) {
            return "Database connection failed! Error: " + e.getMessage();
        }
    }
    @GetMapping("/sell")
    public String createApartmentForm(Model model) {
        Apartment apartment = new Apartment();
        model.addAttribute("apartment", apartment);
        return "sell";
    }
    @PostMapping("/new")
    public String saveApartment(@ModelAttribute("apartment") Apartment apartment) {
        apartmentService.saveApartment(apartment);
        return "redirect:/sell";
    }
}
