package com.example.taskmanager.controller;

import com.example.taskmanager.seeder.DatabaseSeeder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")   // ← これを追加
public class SeedController {

    private final DatabaseSeeder seeder;

    public SeedController(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }

    @PostMapping("/seed")
    public String runSeeder() {
        seeder.seed();
        return "{\"message\":\"Seeder executed\"}";
    }
}
