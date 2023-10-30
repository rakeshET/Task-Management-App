package com.edstem.service;

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = (List<Task>) taskRepository.findAll();
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    public List<TaskResponse> createTasks(List<TaskRequest> taskRequests) {
        List<Task> tasks =
                taskRequests.stream()
                        .map(taskRequest -> modelMapper.map(taskRequest, Task.class))
                        .collect(Collectors.toList());
        tasks = (List<Task>) taskRepository.saveAll(tasks);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    public TaskResponse getTask(Long id) {
        Task task = findTaskById(id);
        if (task == null) {
            throw new RuntimeException("Task not found with id " + id);
        }
        return modelMapper.map(task, TaskResponse.class);
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
        Task task = findTaskById(id);
        task =
                Task.builder()
                        .id(task.getId())
                        .title(
                                Optional.ofNullable(taskUpdateRequest.getTitle())
                                        .orElse(task.getTitle()))
                        .description(
                                Optional.ofNullable(taskUpdateRequest.getDescription())
                                        .orElse(task.getDescription()))
                        .assigneeId(
                                Optional.ofNullable(taskUpdateRequest.getAssigneeId())
                                        .orElse(task.getAssigneeId()))
                        .status(
                                Optional.ofNullable(taskUpdateRequest.getStatus())
                                        .orElse(task.getStatus()))
                        .build();
        task = taskRepository.save(task);
        return modelMapper.map(task, TaskResponse.class);
    }

    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request) {
        Task task =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found"));
        task =
                Task.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .assigneeId(task.getAssigneeId())
                        .status(request.getStatus())
                        .build();
        task = taskRepository.save(task);
        return modelMapper.map(task, TaskResponse.class);
    }

    public List<TaskResponse> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByAssignee(Long assigneeId) {
        List<Task> tasks = taskRepository.findByAssigneeId(assigneeId);
        return tasks.stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .collect(Collectors.toList());
    }

    public CommentResponse addCommentToTask(Long id, CommentRequest request) {
        Task task =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found"));
        Comment comment = Comment.builder().task(task).comment(request.getComment()).build();
        comment = commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponse.class);
    }

    public List<CommentResponse> getTaskComments(Long id) {
        List<Comment> comments = commentRepository.findByTaskId(id);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }
}
