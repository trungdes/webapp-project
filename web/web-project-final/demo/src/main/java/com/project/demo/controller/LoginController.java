package com.project.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.project.demo.model.UserEntity;
import com.project.demo.repository.UserRepository;
import com.project.demo.repository.NotificationRepository;
import com.project.demo.model.Notification;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/")
    public String home(Model model, @SessionAttribute(value = "user", required = false) com.project.demo.model.UserEntity user, @ModelAttribute(value = "notification", binding = false) String notification) {
        int unreadUserNotifications = 0;
        if (user != null) {
            unreadUserNotifications = notificationRepository.findByTenantEmailAndIsReadFalse(user.getEmail()).size();
        }
        model.addAttribute("unreadUserNotifications", unreadUserNotifications);
        if (notification != null && !notification.isEmpty()) {
            model.addAttribute("notification", notification);
        }
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity user = userRepository.findByUsername(username);
            session.setAttribute("user", user);
            
            // Create notification for successful login (for bell)
            Notification notification = Notification.builder()
                .message("Đăng nhập thành công!")
                .isRead(false)
                .createdAt(java.time.LocalDateTime.now().toString())
                .tenantEmail(user.getEmail())
                .build();
            notificationRepository.save(notification);
            
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "index";
        }
    }
}
