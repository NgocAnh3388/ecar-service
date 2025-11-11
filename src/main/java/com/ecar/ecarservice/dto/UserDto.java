package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.entities.MaintenanceHistory;
import com.ecar.ecarservice.entities.SubscriptionInfo;
import com.ecar.ecarservice.entities.Vehicle;
import com.ecar.ecarservice.enums.AppRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNo;
    private Set<AppRole> roles;
    private boolean active;

    private List<VehicleDto> vehicles;
    private List<MaintenanceHistoryDTO> maintenanceHistories;
    private List<SubscriptionInfoDto> subscriptionInfos;

}
