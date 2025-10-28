package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.repositories.AppUserRepository;
import com.ecar.ecarservice.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final AppUserRepository appUserRepository;

    public VehicleController(VehicleService vehicleService, AppUserRepository appUserRepository) {
        this.vehicleService = vehicleService;
        this.appUserRepository = appUserRepository;
    }

    private AppUser getCurrentUser(OidcUser oidcUser) {
        return appUserRepository.findBySub(oidcUser.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDto>> getMyVehicles(@AuthenticationPrincipal OidcUser oidcUser) {
        AppUser currentUser = getCurrentUser(oidcUser);
        return ResponseEntity.ok(vehicleService.getMyVehicles(currentUser));
    }

    @PostMapping
    public ResponseEntity<VehicleDto> addVehicle(@RequestBody VehicleDto vehicleDto, @AuthenticationPrincipal OidcUser oidcUser) {
        AppUser currentUser = getCurrentUser(oidcUser);
        VehicleDto newVehicle = vehicleService.addVehicle(vehicleDto, currentUser);
        return new ResponseEntity<>(newVehicle, HttpStatus.CREATED);
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehicleDto vehicleDto,
            @AuthenticationPrincipal OidcUser oidcUser) {
        AppUser currentUser = getCurrentUser(oidcUser);
        VehicleDto updatedVehicle = vehicleService.updateVehicle(vehicleId, vehicleDto, currentUser);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal OidcUser oidcUser) {
        AppUser currentUser = getCurrentUser(oidcUser);
        vehicleService.deleteVehicle(vehicleId, currentUser);
        return ResponseEntity.noContent().build();
    }
}