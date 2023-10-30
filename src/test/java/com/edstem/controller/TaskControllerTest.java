package com.edstem.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edstem.contract.request.CommentRequest;
import com.edstem.contract.request.TaskRequest;
import com.edstem.contract.request.TaskStatusUpdateRequest;
import com.edstem.contract.request.TaskUpdateRequest;
import com.edstem.contract.response.CommentResponse;
import com.edstem.contract.response.TaskResponse;
import com.edstem.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TaskService taskService;

    @Test
    void testGetAllTasks() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Hello, Mock");
        taskResponse.setDescription("Task Description");
        taskResponse.setAssigneeId(123L);
        taskResponse.setStatus("Task Status");
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(taskResponse));

        mockMvc.perform(get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Hello, Mock")))
                .andExpect(jsonPath("$[0].description", is("Task Description")))
                .andExpect(jsonPath("$[0].assigneeId", is(123)))
                .andExpect(jsonPath("$[0].status", is("Task Status")));
    }

    @Test
    void testCreateTasks() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Task Title");
        taskResponse.setDescription("Task Description");
        taskResponse.setAssigneeId(123L);
        taskResponse.setStatus("Task Status");
        when(taskService.createTasks(any())).thenReturn(Arrays.asList(taskResponse));

        mockMvc.perform(
                        post("/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        new ObjectMapper()
                                                .writeValueAsString(
                                                        Arrays.asList(new TaskRequest()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Task Title")))
                .andExpect(jsonPath("$[0].description", is("Task Description")))
                .andExpect(jsonPath("$[0].assigneeId", is(123)))
                .andExpect(jsonPath("$[0].status", is("Task Status")));
    }

    @Test
    void testGetTask() throws Exception {
        Long id = 1L;
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(id);
        when(taskService.getTask(id)).thenReturn(taskResponse);

        mockMvc.perform(get("/tasks/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testUpdateTask() throws Exception {
        Long id = 1L;
        TaskRequest taskRequest = new TaskRequest();
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(id);
        when(taskService.updateTask(eq(id), any(TaskUpdateRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(
                        put("/tasks/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testDeleteTask() throws Exception {
        Long id = 1L;
        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/" + id)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        Long id = 1L;
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(id);
        taskResponse.setStatus("Updated Status");
        when(taskService.updateTaskStatus(eq(id), any())).thenReturn(taskResponse);

        mockMvc.perform(
                        patch("/tasks/" + id + "/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        new ObjectMapper()
                                                .writeValueAsString(new TaskStatusUpdateRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("Updated Status")));
    }

    @Test
    void testGetTasksByStatus() throws Exception {
        String status = "Task Status";
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setStatus(status);
        when(taskService.getTasksByStatus(status)).thenReturn(Arrays.asList(taskResponse));

        mockMvc.perform(get("/tasks/status/" + status))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is(status)));
    }

    @Test
    void testGetTasksByAssignee() throws Exception {
        Long assigneeId = 123L;
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setAssigneeId(assigneeId);
        when(taskService.getTasksByAssignee(assigneeId)).thenReturn(Arrays.asList(taskResponse));

        mockMvc.perform(get("/tasks/assignee/" + assigneeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assigneeId", equalTo(assigneeId.intValue())));
    }

    @Test
    void testAddCommentToTask() throws Exception {
        Long id = 1L;
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setComment("Test comment");
        when(taskService.addCommentToTask(eq(id), any())).thenReturn(commentResponse);

        mockMvc.perform(
                        post("/tasks/" + id + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        new ObjectMapper()
                                                .writeValueAsString(new CommentRequest())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment", is("Test comment")));
    }

    @Test
    void testGetTaskComments() throws Exception {
        Long id = 1L;
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setComment("Test comment");
        when(taskService.getTaskComments(id)).thenReturn(Arrays.asList(commentResponse));

        mockMvc.perform(get("/tasks/" + id + "/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment", is("Test comment")));
    }
}
