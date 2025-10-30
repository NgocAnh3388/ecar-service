package com.ecar.ecarservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ping")
public class PingController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void ping() {
        System.out.println("ping");
    }
}