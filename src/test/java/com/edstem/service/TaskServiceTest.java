package com.edstem.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edstem.contract.request.CommentRequest;
import com.edstem.contract.request.TaskRequest;
import com.edstem.contract.request.TaskStatusUpdateRequest;
import com.edstem.contract.request.TaskUpdateRequest;
import com.edstem.contract.response.CommentResponse;
import com.edstem.contract.response.TaskResponse;
import com.edstem.model.Comment;
import com.edstem.model.Task;
import com.edstem.repository.CommentRepository;
import com.edstem.repository.TaskRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

public class TaskServiceTest {

    private TaskRepository taskRepository;
    private CommentRepository commentRepository;
    private TaskService taskService;

    private ModelMapper modelMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        taskRepository = mock(TaskRepository.class);
        commentRepository = mock(CommentRepository.class);
        modelMapper = mock(ModelMapper.class);
        taskService = new TaskService(taskRepository, commentRepository, modelMapper);
    }

    @Test
    void testGetAllTasks() {
        List<Task> mockTasks = Arrays.asList(new Task());
        when(taskRepository.findAll()).thenReturn(mockTasks);

        List<TaskResponse> taskResponses = taskService.getAllTasks();

        verify(taskRepository, times(1)).findAll();

        assertEquals(mockTasks.size(), taskResponses.size());
    }

    @Test
    void testCreateTasks() {
        List<TaskRequest> taskRequests = Arrays.asList(new TaskRequest());
        when(taskRepository.saveAll(any())).thenReturn(Arrays.asList(new Task()));
        when(modelMapper.map(any(TaskRequest.class), eq(Task.class))).thenReturn(new Task());
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class)))
                .thenReturn(new TaskResponse());

        List<TaskResponse> tasks = taskService.createTasks(taskRequests);

        assertEquals(1, tasks.size());
        verify(modelMapper, times(1)).map(any(TaskRequest.class), eq(Task.class));
        verify(modelMapper, times(1)).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    void testGetTask() {
        Optional<Task> ofResult = Optional.of(new Task());
        when(taskRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setAssigneeId(1L);
        taskResponse.setDescription("The characteristics of someone or something");
        taskResponse.setId(1L);
        taskResponse.setStatus("Status");
        taskResponse.setTitle("Dr");
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<TaskResponse>>any()))
                .thenReturn(taskResponse);
        TaskResponse actualTask = taskService.getTask(1L);
        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<TaskResponse>>any());
        verify(taskRepository).findById(Mockito.<Long>any());
        assertSame(taskResponse, actualTask);
    }

    @Test
    void testGetTaskThrowsException() {

        Long id = 1L;
        TaskRepository taskRepository = mock(TaskRepository.class);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> {
                            taskService.getTask(id);
                        });

        assertThat(exception.getMessage(), containsString("Task not found with id " + id));
    }

    @Test
    void testUpdateTaskById() {
        Long id = 1L;
        TaskUpdateRequest taskUpdateRequest = new TaskUpdateRequest();
        Task task = new Task();
        Task updatedTask = new Task();
        TaskResponse taskResponse = new TaskResponse();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(id, taskUpdateRequest);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(modelMapper, times(1)).map(any(Task.class), eq(TaskResponse.class));
    }

    @Test
    void testDeleteTaskById() {
        Long id = 1L;
        Task task = new Task();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(id);

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testUpdateTaskByStatus() {
        Long id = 1L;
        String status = "completed";
        Task task = Task.builder().id(id).status("in progress").build();
        Task updatedTask = Task.builder().id(id).status(status).build();
        TaskStatusUpdateRequest taskStatusUpdateRequest = new TaskStatusUpdateRequest();
        taskStatusUpdateRequest.setStatus(status);
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setStatus(status);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTaskStatus(id, taskStatusUpdateRequest);

        assertNotNull(result);
        assertEquals(result.getStatus(), status);
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testGetTasksByStatus() {
        String status = "in progress";

        List<Task> tasks = new ArrayList<>();
        tasks.add(Task.builder().id(1L).status(status).build());
        tasks.add(Task.builder().id(2L).status(status).build());
        tasks.add(
                Task.builder()
                        .id(3L)
                        .status("completed")
                        .build());

        when(taskRepository.findByStatus(status)).thenReturn(tasks);

        List<TaskResponse> result = taskService.getTasksByStatus(status);

        List<TaskResponse> expectedResponses =
                tasks.stream()
                        .map(task -> modelMapper.map(task, TaskResponse.class))
                        .collect(Collectors.toList());

        assertEquals(expectedResponses, result);
    }

    @Test
    void testGetTasksByAssignee() {
        Long assigneeId = 1L;
        Task task = Task.builder().id(1L).assigneeId(assigneeId).build();
        when(taskRepository.findByAssigneeId(assigneeId)).thenReturn(Arrays.asList(task));

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        when(modelMapper.map(any(Task.class), eq(TaskResponse.class))).thenReturn(taskResponse);

        List<TaskResponse> tasks = taskService.getTasksByAssignee(assigneeId);

        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.get(0).getId());
    }

    @Test
    void testAddCommentToTask() {
        Long taskId = 1L;
        String commentText = "New Comment";

        Task task = Task.builder().id(taskId).status("in progress").build();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setComment(commentText);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Comment addedComment = Comment.builder().id(2L).task(task).comment(commentText).build();

        when(commentRepository.save(any(Comment.class))).thenReturn(addedComment);

        CommentResponse result = taskService.addCommentToTask(taskId, commentRequest);

        CommentResponse expectedResponse = modelMapper.map(addedComment, CommentResponse.class);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetTaskComments() {
        Long taskId = 1L;

        Task task = Task.builder().id(taskId).status("in progress").build();

        Comment comment1 = Comment.builder().id(1L).task(task).comment("Comment 1").build();
        Comment comment2 = Comment.builder().id(2L).task(task).comment("Comment 2").build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(taskId)).thenReturn(Arrays.asList(comment1, comment2));

        List<CommentResponse> result = taskService.getTaskComments(taskId);

        assertEquals(2, result.size());
    }
}
