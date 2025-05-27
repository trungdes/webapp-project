package com.project.demo.service;

import com.project.demo.dto.RegistrationDto;
import com.project.demo.model.UserEntity;
public interface UserService {

    void saveUser(RegistrationDto registrationDto);
    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
} 

