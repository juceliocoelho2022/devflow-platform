package com.devflow.service;

import com.devflow.dto.ApiDtos.*;
import com.devflow.mapper.ApiMapper;
import com.devflow.model.*;
import com.devflow.repository.ProjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
  private final ProjectRepository projects;
  public ProjectService(ProjectRepository projects) { this.projects = projects; }

  @Transactional(readOnly = true)
  public List<ProjectView> findAll() {
    return projects.findAll().stream().map(ApiMapper::toView).toList();
  }

  @Transactional
  public ProjectView create(ProjectRequest request) {
    return ApiMapper.toView(projects.save(new Project(
      request.name().trim(), request.description(), request.status() == null ? ProjectStatus.PLANNING : request.status()
    )));
  }
}
