package com.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    ResponseEntity<?> hello() {
        return ResponseEntity.status(HttpStatus.OK)
                .body("Helloo");
    }
}
