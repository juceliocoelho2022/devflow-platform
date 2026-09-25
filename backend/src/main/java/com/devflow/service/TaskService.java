package com.devflow.service;

import com.devflow.dto.ApiDtos.*;
import com.devflow.exception.BusinessException;
import com.devflow.mapper.ApiMapper;
import com.devflow.model.*;
import com.devflow.repository.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
  private final TaskRepository tasks;
  private final ProjectRepository projects;
  private final UserRepository users;

  public TaskService(TaskRepository tasks, ProjectRepository projects, UserRepository users) {
    this.tasks = tasks; this.projects = projects; this.users = users;
  }

  @Transactional(readOnly = true)
  public List<TaskView> findAll(String search, TaskStatus status, Priority priority, Long projectId) {
    String normalized = search == null || search.isBlank() ? null : search.trim().toLowerCase();
    return tasks.search(normalized, status, priority, projectId).stream().map(ApiMapper::toView).toList();
  }

  @Transactional
  public TaskView create(TaskRequest request) {
    var project = projects.findById(request.projectId())
      .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "Projeto não encontrado."));
    var assignee = request.assigneeId() == null ? null : users.findById(request.assigneeId())
      .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Responsável não encontrado."));
    return ApiMapper.toView(tasks.save(new Task(
      request.title().trim(), request.description(),
      request.status() == null ? TaskStatus.BACKLOG : request.status(),
      request.priority() == null ? Priority.MEDIUM : request.priority(),
      project, assignee, request.dueDate()
    )));
  }

  @Transactional
  public TaskView move(Long id, TaskStatus status) {
    var task = tasks.findById(id)
      .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND", "Tarefa não encontrada."));
    task.setStatus(status);
    return ApiMapper.toView(task);
  }
}
