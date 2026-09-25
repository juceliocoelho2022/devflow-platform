package com.devflow.controller;

import com.devflow.dto.ApiDtos.ProjectRequest;
import com.devflow.dto.ApiDtos.ProjectView;
import com.devflow.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/projects")
public class ProjectController {
  private final ProjectService projects;
  public ProjectController(ProjectService projects){this.projects=projects;}
  @GetMapping public List<ProjectView> all(){return projects.findAll();}
  @PostMapping public ResponseEntity<ProjectView> create(@Valid @RequestBody ProjectRequest req){
    return ResponseEntity.status(201).body(projects.create(req));
  }
}
