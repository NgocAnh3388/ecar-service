package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.entities.CarModel;
import com.ecar.ecarservice.repositories.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-model") // Đường dẫn chuẩn số nhiều
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CarModelController {

    private final CarModelRepository carModelRepository;

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<List<CarModel>> getAllCarModels() {
        return ResponseEntity.ok(carModelRepository.findAll());
    }
}