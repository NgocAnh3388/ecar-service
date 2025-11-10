package com.ecar.ecarservice.service;

import com.ecar.ecarservice.payload.responses.CarModelResponse;

import java.util.List;

public interface CarModelService {
    List<CarModelResponse> getAll();
}
