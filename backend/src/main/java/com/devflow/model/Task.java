package com.devflow.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "tasks")
public class Task {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String title;
  @Column(length=1000) private String description;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private TaskStatus status;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Priority priority;
  @ManyToOne(optional=false) private Project project;
  @ManyToOne private User assignee;
  private LocalDate dueDate;
  @Column(nullable=false) private LocalDateTime createdAt;
  protected Task(){}
  public Task(String title,String description,TaskStatus status,Priority priority,Project project,User assignee,LocalDate dueDate){
    this.title=title;this.description=description;this.status=status;this.priority=priority;this.project=project;this.assignee=assignee;this.dueDate=dueDate;this.createdAt=LocalDateTime.now();
  }
  public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
  public TaskStatus getStatus(){return status;} public Priority getPriority(){return priority;} public Project getProject(){return project;}
  public User getAssignee(){return assignee;} public LocalDate getDueDate(){return dueDate;} public LocalDateTime getCreatedAt(){return createdAt;}
  public void setStatus(TaskStatus status){this.status=status;}
}
