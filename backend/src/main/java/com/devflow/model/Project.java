package com.devflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  private String description;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private ProjectStatus status;
  @Column(nullable=false) private LocalDateTime createdAt;
  protected Project() {}
  public Project(String name,String description,ProjectStatus status){this.name=name;this.description=description;this.status=status;this.createdAt=LocalDateTime.now();}
  public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
  public ProjectStatus getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
}
