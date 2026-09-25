package com.devflow.mapper;

import com.devflow.dto.ApiDtos.ProjectView;
import com.devflow.dto.ApiDtos.TaskView;
import com.devflow.dto.ApiDtos.UserView;
import com.devflow.model.Project;
import com.devflow.model.Task;
import com.devflow.model.User;

public final class ApiMapper {
  private ApiMapper() {}

  public static UserView toView(User user) {
    return user == null ? null : new UserView(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  public static ProjectView toView(Project project) {
    return new ProjectView(project.getId(), project.getName(), project.getDescription(), project.getStatus(), project.getCreatedAt());
  }

  public static TaskView toView(Task task) {
    return new TaskView(
      task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(),
      toView(task.getProject()), toView(task.getAssignee()), task.getDueDate(), task.getCreatedAt()
    );
  }
}
