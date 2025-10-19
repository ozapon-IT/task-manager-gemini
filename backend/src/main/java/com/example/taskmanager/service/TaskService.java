package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findByProjectId(Long projectId) {
        // @Where(clause = "deleted_at IS NULL") が有効なので削除済みは自動除外
        return taskRepository.findByProjectId(projectId);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            // 新規作成
            return taskRepository.save(task);
        }

        // 更新処理
        Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + task.getId()));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setProject(task.getProject()); // 関連保持

        return taskRepository.save(existingTask);
    }

    public void deleteById(Long id) {
        // @SQLDelete により deleted_at に日時が入り、物理削除されない
        taskRepository.deleteById(id);
    }
}
