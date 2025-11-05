package com.ecar.ecarservice.service.impl;

import com.ecar.ecarservice.enitiies.CarModel;
import com.ecar.ecarservice.payload.responses.CarModelResponse;
import com.ecar.ecarservice.repositories.CarModelRepository;
import com.ecar.ecarservice.service.CarModelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarModelServiceImpl implements CarModelService {

    private final CarModelRepository carModelRepository;

    public CarModelServiceImpl(CarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    @Override
    public List<CarModelResponse> getAll() {
        return this.carModelRepository.findAll()
                .stream()
                .map(this::fromCarModel)
                .toList();
    }

    private CarModelResponse fromCarModel(CarModel carModel) {
        return new CarModelResponse(
                carModel.getId(),
                carModel.getCarName(),
                carModel.getCarType()
        );
    }
}
