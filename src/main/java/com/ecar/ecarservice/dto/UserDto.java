package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.AppRole;

import java.util.Set;

public class UserDto {
    private Long id;
    private String email;
    private Set<AppRole> roles;
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<AppRole> getRoles() { return roles; }
    public void setRoles(Set<AppRole> roles) { this.roles = roles; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}