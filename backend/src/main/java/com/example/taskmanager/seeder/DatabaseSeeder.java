package com.example.taskmanager.seeder;

import com.example.taskmanager.entity.*;
import com.example.taskmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseSeeder {

    private final ProjectRepository projectRepository;

    public DatabaseSeeder(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void seed() {
        if (projectRepository.count() > 0) {
            System.out.println("⚠️  Data already exists. Skipping seeding.");
            return;
        }

        List<Project> projects = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Project project = new Project();
            project.setTitle("Manual Seed Project " + i);
            project.setDescription("This is a manually seeded project.");
            project.setDueDate(LocalDate.now().plusDays(5 * i));
            project.setStatus(ProjectStatus.ACTIVE);

            List<Task> tasks = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                Task task = new Task();
                task.setProject(project);
                task.setTitle("Task " + j + " for Project " + i);
                task.setDescription("Sample description " + j);
                task.setDueDate(LocalDate.now().plusDays(j));
                task.setStatus(TaskStatus.TODO);
                task.setPriority(TaskPriority.MEDIUM);
                tasks.add(task);
            }

            project.setTasks(tasks);
            projects.add(project);
        }

        projectRepository.saveAll(projects);
        System.out.println("✅ Manual seeding completed successfully.");
    }
}
