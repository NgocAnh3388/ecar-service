package com.ecar.ecarservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    private String phoneNo;

    @NotBlank(message = "Role cannot be blank")
    private String role;
}
