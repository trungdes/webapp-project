package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.demo.model.UserEntity;
import com.project.demo.repository.UserRepository;
import com.project.demo.dto.RegistrationDto;
import com.project.demo.service.UserService;

@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Kiểm tra các trường bắt buộc
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập không được để trống!");
            return "register";
        }
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email không được để trống!");
            return "register";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Mật khẩu không được để trống!");
            return "register";
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            model.addAttribute("error", "Họ không được để trống!");
            return "register";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            model.addAttribute("error", "Tên không được để trống!");
            return "register";
        }

        // Kiểm tra độ dài username
        if (username.length() < 3) {
            model.addAttribute("error", "Tên đăng nhập phải có ít nhất 3 ký tự!");
            return "register";
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "Email không hợp lệ!");
            return "register";
        }

        // Kiểm tra độ dài password
        if (password.length() < 8) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự!");
            return "register";
        }

        // Kiểm tra username/email đã tồn tại chưa
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "register";
        }

        try {
            RegistrationDto registrationDto = new RegistrationDto();
            registrationDto.setUsername(username);
            registrationDto.setEmail(email);
            registrationDto.setPassword(passwordEncoder.encode(password));
            // Nếu muốn lưu firstName, lastName thì cần sửa UserServiceImpl
            userService.saveUser(registrationDto);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại sau!");
            return "register";
        }
    }
}
