package com.ecar.ecarservice.payload.requests;

import lombok.Data;

@Data
public class MaintenanceHistorySearchRequest {
    private String searchValue;
    private int page;
    private int size;
}
