package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.enitiies.Center;
import com.ecar.ecarservice.payload.responses.CenterResponse;
import com.ecar.ecarservice.repositories.CenterRepository;
import com.ecar.ecarservice.service.CenterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CenterServiceImpl implements CenterService {
    private final CenterRepository centerRepository;

    public CenterServiceImpl(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    public List<CenterResponse> getAllCenter() {
        return centerRepository.findAll()
                .stream()
                .map(this::fromCenter)
                .toList();
    }

    private CenterResponse fromCenter(Center center) {
        return new CenterResponse(center.getId(), center.getCenterName());
    }
}
