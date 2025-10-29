package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.responses.CarModelResponse;
import com.ecar.ecarservice.service.CarModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/car-model")
public class CarModelController {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<CarModelResponse>> getAll() {
        return ResponseEntity.ok(this.carModelService.getAll());
    }
}