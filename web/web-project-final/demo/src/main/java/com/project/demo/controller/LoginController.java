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

import com.project.demo.model.UserEntity;
import com.project.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    //    @PostMapping("/login")
    //    public String login(@RequestParam String username,
    //                       @RequestParam String password,
    //                       Model model, HttpSession session) {
    //        try {
    //            Authentication authentication = authenticationManager.authenticate(
    //                new UsernamePasswordAuthenticationToken(username, password)
    //            );
    //            
    //            SecurityContextHolder.getContext().setAuthentication(authentication);
    //            UserEntity user = userRepository.findByUsername(username);
    //            session.setAttribute("user", user);
    //            
    //            return "redirect:/";
    //        } catch (Exception e) {
    //            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
    //            return "index";
    //        }
}
