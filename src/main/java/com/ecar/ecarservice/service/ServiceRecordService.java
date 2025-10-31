package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;

import java.util.List;

public interface ServiceRecordService {
    ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request);
    List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate);
}
