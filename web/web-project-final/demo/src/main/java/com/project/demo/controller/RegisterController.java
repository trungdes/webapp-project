package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.project.demo.model.UserEntity;
import com.project.demo.repository.UserRepository;

@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        // Kiểm tra username/email đã tồn tại chưa
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "register";
        }
        // Tạo user mới
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Nên mã hóa password!
        userRepository.save(user);
        model.addAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
        return "register";
    }
}
