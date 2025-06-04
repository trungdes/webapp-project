package com.project.demo.controller;

import com.project.demo.model.Apartment;
import com.project.demo.model.ViewingSchedule;
import com.project.demo.model.Notification;
import com.project.demo.repository.ApartmentRepository;
import com.project.demo.repository.ViewingScheduleRepository;
import com.project.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class ViewingScheduleController {
    @Autowired
    private ViewingScheduleRepository viewingScheduleRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping("/schedule-viewing")
    public String scheduleViewing(
        @RequestParam String apartmentNumber,
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String phone,
        @RequestParam String notes,
        @RequestParam String date,
        @RequestParam String time,
        Model model
    ) {
        Apartment apartment = apartmentRepository.findById(apartmentNumber).orElse(null);
        ViewingSchedule schedule = ViewingSchedule.builder()
            .apartment(apartment)
            .name(name)
            .email(email)
            .phone(phone)
            .notes(notes)
            .date(date)
            .time(time)
            .build();
        viewingScheduleRepository.save(schedule);

        // Tạo notification
        Notification notification = Notification.builder()
            .apartment(apartment)
            .message("New viewing request for apartment " + (apartment != null ? apartment.getApartmentNumber() : "N/A"))
            .isRead(false)
            .createdAt(java.time.LocalDateTime.now().toString())
            .build();
        notificationRepository.save(notification);

        model.addAttribute("message", "Lịch xem nhà đã được đặt thành công!");
        return "redirect:/apartments/" + apartmentNumber;
    }

    @GetMapping("/admin/viewing-schedules")
    public String adminViewingSchedules(Model model) {
        model.addAttribute("viewingSchedules", viewingScheduleRepository.findAll());
        return "admin";
    }
} 