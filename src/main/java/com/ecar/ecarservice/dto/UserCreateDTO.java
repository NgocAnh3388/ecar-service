package com.ecar.ecarservice.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String email;

    private String fullName;

    private String phoneNo;

    private String role;
}
