package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.demo.model.UserEntity;
import com.project.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model, HttpSession session) {
    UserEntity user = userRepository.findByUsername(username);
        if (user != null) {
            // Nếu password đã hash, dùng BCrypt để so sánh
            if (user.getPassword().equals(password) && user.getUsername().equals(username)) { // Đơn giản, chưa hash
                session.setAttribute("user", user);
                return "redirect:/"; // hoặc trang dashboard
            }
        }
        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
        return "index"; // hoặc trả về modal với lỗi
    }
}
