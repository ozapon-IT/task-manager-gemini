package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectStatus;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TaskControllerFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long projectId;

    @BeforeEach
    void setup() throws Exception {
        // Create a project before each test to associate tasks with it
        Project newProject = new Project();
        newProject.setTitle("Task Test Project");
        newProject.setDescription("Project for task feature tests");
        newProject.setStatus(ProjectStatus.ACTIVE);

        MvcResult postResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andReturn();

        projectId = objectMapper.readTree(postResult.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void taskCrudFlow() throws Exception {
        // 1. Create a new task (POST /api/projects/{projectId}/tasks)
        Task newTask = new Task();
        newTask.setTitle("Test Task");
        newTask.setDescription("Description for Test Task");
        newTask.setStatus(TaskStatus.TODO);
        newTask.setPriority(TaskPriority.MEDIUM);

        MvcResult postResult = mockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description for Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andReturn();

        Long taskId = objectMapper.readTree(postResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Get the created task (GET /api/projects/{projectId}/tasks/{taskId})
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{taskId}", projectId, taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description for Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));

        // 3. Update the task (PUT /api/projects/{projectId}/tasks/{taskId})
        Task updatedTask = new Task();
        updatedTask.setId(taskId); // ID must be set for update
        updatedTask.setTitle("Updated Test Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(TaskStatus.DONE);
        updatedTask.setPriority(TaskPriority.HIGH);

        mockMvc.perform(put("/api/projects/{projectId}/tasks/{taskId}", projectId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Test Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        // 4. Get all tasks for the project and verify the updated task is there (GET /api/projects/{projectId}/tasks)
        mockMvc.perform(get("/api/projects/{projectId}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1)))) // At least one task
                .andExpect(jsonPath("$[?(@.id == %d)].title", taskId).value(hasItem("Updated Test Task")));

        // 5. Delete the task (DELETE /api/projects/{projectId}/tasks/{taskId})
        mockMvc.perform(delete("/api/projects/{projectId}/tasks/{taskId}", projectId, taskId))
                .andExpect(status().isNoContent());

        // 6. Try to get the deleted task (GET /api/projects/{projectId}/tasks/{taskId}) - should be not found
        mockMvc.perform(get("/api/projects/{projectId}/tasks/{taskId}", projectId, taskId))
                .andExpect(status().isNotFound());
    }
}
