package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.VehicleDto;
import com.ecar.ecarservice.enitiies.Vehicle;
import com.ecar.ecarservice.payload.requests.VehicleRequest;
import com.ecar.ecarservice.payload.responses.VehicleResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

public interface VehicleService {
    List<VehicleResponse> getMyVehicles(OidcUser oidcUser);
    void addVehicle(VehicleRequest vehicleDto, OidcUser oidcUser);
    VehicleDto updateVehicle(Long vehicleId, VehicleDto vehicleDto, OidcUser oidcUser);
    void deleteVehicle(Long vehicleId, OidcUser oidcUser);
//    List<VehicleDashboardInfoDTO> getVehicleDashboardInfo(OidcUser oidcUser);
}
