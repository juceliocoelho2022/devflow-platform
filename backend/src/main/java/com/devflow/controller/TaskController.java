package com.devflow.controller;

import com.devflow.dto.ApiDtos.*;
import com.devflow.model.*;
import com.devflow.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService tasks;
  public TaskController(TaskService tasks){this.tasks=tasks;}
  @GetMapping public List<TaskView> all(
    @RequestParam(required=false) String search,
    @RequestParam(required=false) TaskStatus status,
    @RequestParam(required=false) Priority priority,
    @RequestParam(required=false) Long projectId
  ){return tasks.findAll(search,status,priority,projectId);}
  @PostMapping public ResponseEntity<TaskView> create(@Valid @RequestBody TaskRequest req){
    return ResponseEntity.status(201).body(tasks.create(req));
  }
  @PatchMapping("/{id}/status") public TaskView status(@PathVariable Long id,@Valid @RequestBody StatusRequest req){
    return tasks.move(id,req.status());
  }
}
