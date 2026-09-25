package com.devflow.controller;

import com.devflow.dto.ApiDtos.Dashboard;
import com.devflow.model.*;
import com.devflow.repository.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
  private final ProjectRepository projects;private final TaskRepository tasks;private final UserRepository users;
  public DashboardController(ProjectRepository projects,TaskRepository tasks,UserRepository users){this.projects=projects;this.tasks=tasks;this.users=users;}
  @GetMapping public Dashboard dashboard(){
    var by=new EnumMap<TaskStatus,Long>(TaskStatus.class);
    for(var status:TaskStatus.values())by.put(status,tasks.countByStatus(status));
    return new Dashboard(projects.count(),by.get(TaskStatus.TODO)+by.get(TaskStatus.DOING)+by.get(TaskStatus.REVIEW),by.get(TaskStatus.DONE),users.count(),by,
      List.of(20,42,27,41,76,90,78,96),List.of("Nova tarefa criada: Implementar autenticação JWT","Tarefa concluída: Ajustes no layout do dashboard","Novo projeto criado: Módulo de Relatórios","Usuário adicionado ao projeto DevFlow Web App"));
  }
}
