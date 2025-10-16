package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> findByProjectId(@PathVariable Long projectId) {
        return taskService.findByProjectId(projectId);
    }

    @GetMapping("/{id}")
    public Task findById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    public Task save(@PathVariable Long projectId, @RequestBody Task task) {
        task.setProjectId(projectId);
        return taskService.save(task);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long projectId, @PathVariable Long id, @RequestBody Task task) {
        task.setProjectId(projectId);
        task.setId(id);
        return taskService.save(task);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        taskService.deleteById(id);
    }
}
