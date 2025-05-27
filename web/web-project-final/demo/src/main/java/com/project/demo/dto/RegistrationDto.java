package com.project.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationDto {
    private long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty   
    private String password;
}
