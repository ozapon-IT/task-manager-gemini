package com.example.taskmanager.service;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.ProjectRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public Page<Project> findAll(Pageable pageable) {
        // @Where(clause = "deleted_at IS NULL") により自動フィルタされる
        return projectRepository.findAll(pageable);
    }

    public Optional<Project> findById(Long id) {
        // @Where により deleted_at IS NULL のみ取得される
        return projectRepository.findById(id);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteById(Long id) {
        // プロジェクトが存在するか確認
        projectRepository.findById(id).ifPresent(project -> {
            // 紐づくタスクを論理削除
            List<Task> tasks = taskRepository.findByProjectId(project.getId());
            taskRepository.deleteAll(tasks);

            // プロジェクトを論理削除
            projectRepository.delete(project);
        });
    }
}