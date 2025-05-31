package com.project.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.demo.model.Role;
import com.project.demo.model.UserEntity;
import com.project.demo.repository.RoleRepository;
import com.project.demo.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Bắt đầu khởi tạo dữ liệu...");
            
            // Tạo các roles nếu chưa tồn tại
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                logger.info("Tạo role USER...");
                Role newUserRole = new Role();
                newUserRole.setName("USER");
                roleRepository.save(newUserRole);
                logger.info("Đã tạo role USER thành công");
                userRole = roleRepository.findByName("USER");
            } else {
                logger.info("Role USER đã tồn tại");
            }

            Role managerRole = roleRepository.findByName("MANAGER");
            if (managerRole == null) {
                logger.info("Tạo role MANAGER...");
                Role newManagerRole = new Role();
                newManagerRole.setName("MANAGER");
                roleRepository.save(newManagerRole);
                logger.info("Đã tạo role MANAGER thành công");
                managerRole = roleRepository.findByName("MANAGER");
            } else {
                logger.info("Role MANAGER đã tồn tại");
            }

            // Tạo tài khoản admin nếu chưa tồn tại
            if (userRepository.findByUsername("admin") == null) {
                logger.info("Tạo tài khoản admin...");
                UserEntity admin = new UserEntity();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@example.com");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRoles(Collections.singletonList(managerRole));
                userRepository.save(admin);
                logger.info("Đã tạo tài khoản admin thành công");
            } else {
                logger.info("Tài khoản admin đã tồn tại");
            }
            
            logger.info("Khởi tạo dữ liệu hoàn tất");
        } catch (Exception e) {
            logger.error("Lỗi khi khởi tạo dữ liệu: " + e.getMessage(), e);
            throw e;
        }
    }
} 