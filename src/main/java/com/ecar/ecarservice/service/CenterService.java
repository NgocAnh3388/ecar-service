package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.responses.CenterResponse;

import java.util.List;

public interface CenterService {
    List<CenterResponse> getAllCenter();
}
