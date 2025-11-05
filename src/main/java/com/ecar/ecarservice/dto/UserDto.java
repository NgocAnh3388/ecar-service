package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.AppRole;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNo;
    private Set<AppRole> roles;
    private boolean active;

    private List<VehicleDto> vehicles;
}
