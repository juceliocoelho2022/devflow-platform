package com.devflow.dto;

import com.devflow.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class ApiDtos {
  private ApiDtos(){}
  public record LoginRequest(@Email String email,@NotBlank String password){}
  public record LoginResponse(String token,UserView user){}
  public record UserView(Long id,String name,String email,Role role){}
  public record ProjectRequest(@NotBlank String name,String description,ProjectStatus status){}
  public record ProjectView(Long id,String name,String description,ProjectStatus status,LocalDateTime createdAt){}
  public record TaskRequest(@NotBlank String title,String description,TaskStatus status,Priority priority,@NotNull Long projectId,Long assigneeId,LocalDate dueDate){}
  public record StatusRequest(@NotNull TaskStatus status){}
  public record TaskView(Long id,String title,String description,TaskStatus status,Priority priority,ProjectView project,UserView assignee,LocalDate dueDate,LocalDateTime createdAt){}
  public record Dashboard(long activeProjects,long inProgress,long completed,long members,Map<TaskStatus,Long> byStatus,List<Integer> productivity,List<String> activities){}
  public record ApiError(int status,String code,String message,LocalDateTime timestamp,Map<String,String> fields){}
}
