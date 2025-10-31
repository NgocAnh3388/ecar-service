package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.AppRole;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private Set<AppRole> roles;
    private boolean active;
}
