package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService,
                          ProjectRepository projectRepository,
                          TaskRepository taskRepository) {
        this.taskService = taskService;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> findByProjectId(@PathVariable Long projectId) {
        // @Where が効くので削除済みは除外される
        return taskRepository.findByProjectId(projectId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task save(@PathVariable Long projectId, @RequestBody Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        task.setProject(project);
        return taskService.save(task);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long projectId, @PathVariable Long id, @RequestBody Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        task.setProject(project);
        task.setId(id);
        return taskService.save(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
