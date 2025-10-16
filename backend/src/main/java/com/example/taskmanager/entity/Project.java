package com.example.taskmanager.entity;

import lombok.Data;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
