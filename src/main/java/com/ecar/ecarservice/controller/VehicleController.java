package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.payload.requests.VehicleRequest;
import com.ecar.ecarservice.payload.responses.VehicleResponse;
import com.ecar.ecarservice.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(vehicleService.getMyVehicles(oidcUser));
    }

    @PostMapping
    public ResponseEntity<Void> addVehicle(@RequestBody VehicleRequest request, @AuthenticationPrincipal OidcUser oidcUser) {
        vehicleService.addVehicle(request, oidcUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody VehicleDto vehicleDto,
            @AuthenticationPrincipal OidcUser oidcUser) {
        VehicleDto updatedVehicle = vehicleService.updateVehicle(vehicleId, vehicleDto, oidcUser);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal OidcUser oidcUser) {
        vehicleService.deleteVehicle(vehicleId, oidcUser);
        return ResponseEntity.noContent().build();
    }
}
