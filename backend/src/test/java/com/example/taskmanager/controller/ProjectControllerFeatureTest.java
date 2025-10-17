package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Project;
import com.example.taskmanager.entity.ProjectStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;
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
public class ProjectControllerFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void projectCrudFlow() throws Exception {
        // 1. Create a new project (POST /api/projects)
        Project newProject = new Project();
        newProject.setTitle("Test Project");
        newProject.setDescription("Description for Test Project");
        newProject.setStatus(ProjectStatus.ACTIVE);

        MvcResult postResult = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Description for Test Project"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        Long projectId = objectMapper.readTree(postResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Get the created project (GET /api/projects/{id})
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Description for Test Project"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // 3. Update the project (PUT /api/projects/{id})
        Project updatedProject = new Project();
        updatedProject.setId(projectId); // ID must be set for update
        updatedProject.setTitle("Updated Test Project");
        updatedProject.setDescription("Updated Description");
        updatedProject.setStatus(ProjectStatus.COMPLETED);

        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("Updated Test Project"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // 4. Get all projects and verify the updated project is there (GET /api/projects)
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[?(@.id == %d)].title", projectId)
                        .value(contains("Updated Test Project")));


        // 5. Delete the project (DELETE /api/projects/{id})
        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        // 6. Try to get the deleted project (GET /api/projects/{id}) - should be not found
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isNotFound());
    }
}
