package com.ecar.ecarservice.service;

import com.ecar.ecarservice.dto.CreateServiceRecordRequest;
import com.ecar.ecarservice.dto.ServiceDetailDto;
import com.ecar.ecarservice.dto.ServiceRecordResponseDto;

import java.util.List;

public interface ServiceRecordService {
    ServiceRecordResponseDto createServiceRecord(CreateServiceRecordRequest request);
    List<ServiceRecordResponseDto> getHistoryByLicensePlate(String licensePlate);
    ServiceRecordResponseDto addDetailToRecord(Long recordId, ServiceDetailDto detailDto);
    ServiceRecordResponseDto getServiceRecordById(Long id);
}