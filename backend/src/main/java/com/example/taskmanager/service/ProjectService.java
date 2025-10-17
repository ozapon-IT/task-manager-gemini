package com.example.taskmanager.service;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    public void deleteById(Long id) {
        projectRepository.deleteById(id); // SQLDeleteでdeleted_atを更新
    }
}
