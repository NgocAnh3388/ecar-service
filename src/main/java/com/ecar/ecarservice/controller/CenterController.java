package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.payload.responses.CenterResponse;
import com.ecar.ecarservice.service.CenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/center")
public class CenterController {

    private final CenterService centerService;

    public CenterController(CenterService centerService) {
        this.centerService = centerService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<CenterResponse>> getAllCenter() {
        return ResponseEntity.ok(this.centerService.getAllCenter());
    }

}