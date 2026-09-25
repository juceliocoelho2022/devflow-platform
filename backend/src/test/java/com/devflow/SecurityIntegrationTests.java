package com.devflow;

import com.devflow.model.*;
import com.devflow.repository.*;
import com.devflow.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="app.jwt.secret=integration-tests-only-key-with-at-least-32-bytes")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTests {
  @Autowired MockMvc mvc;
  @Autowired UserRepository users;
  @Autowired ProjectRepository projects;
  @Autowired TaskRepository tasks;
  @Autowired PasswordEncoder encoder;
  @Autowired JwtService jwt;
  @Autowired ObjectMapper json;

  String login(Role role) throws Exception {
    String email=role.name().toLowerCase()+"@test.example";
    users.save(new User("Test user",email,encoder.encode("test-password"),role));
    var response=mvc.perform(post("/api/auth/login").contentType("application/json")
      .content("{\"email\":\""+email+"\",\"password\":\"test-password\"}"))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return "Bearer "+json.readTree(response).get("token").asText();
  }

  @Test void noDemoUsersOutsideDevelopment(){assertThat(users.count()).isZero();}

  @Test void anonymousAndInvalidTokensReturnJson401() throws Exception {
    mvc.perform(get("/api/projects")).andExpect(status().isUnauthorized())
      .andExpect(jsonPath("code").value("UNAUTHORIZED"));
    mvc.perform(get("/api/projects").header("Authorization","Bearer invalid"))
      .andExpect(status().isUnauthorized());
  }

  @Test void expiredTokenIsRejected() throws Exception {
    users.save(new User("Expired","expired@test.example",encoder.encode("password"),Role.ADMIN));
    var expired=new JwtService("integration-tests-only-key-with-at-least-32-bytes",-1000);
    mvc.perform(get("/api/projects").header("Authorization","Bearer "+expired.generate("expired@test.example","ADMIN")))
      .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest @EnumSource(Role.class)
  void rolesCanReadAndMoveTasks(Role role) throws Exception {
    String token=login(role);
    var project=projects.save(new Project("Project","",ProjectStatus.PLANNING));
    var task=tasks.save(new Task("Task","",TaskStatus.BACKLOG,Priority.MEDIUM,project,null,null));
    for(String path:new String[]{"/api/projects","/api/tasks","/api/dashboard"})
      mvc.perform(get(path).header("Authorization",token)).andExpect(status().isOk());
    mvc.perform(patch("/api/tasks/"+task.getId()+"/status").header("Authorization",token)
      .contentType("application/json").content("{\"status\":\"DONE\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("status").value("DONE"));
    assertThat(tasks.findById(task.getId()).orElseThrow().getStatus()).isEqualTo(TaskStatus.DONE);
  }

  @ParameterizedTest @EnumSource(value=Role.class,names={"ADMIN","MANAGER"})
  void managersCanCreateProjectsAndTasks(Role role) throws Exception {
    String token=login(role);
    String body=mvc.perform(post("/api/projects").header("Authorization",token)
      .contentType("application/json").content("{\"name\":\"New project\"}"))
      .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    long projectId=json.readTree(body).get("id").asLong();
    mvc.perform(post("/api/tasks").header("Authorization",token).contentType("application/json")
      .content("{\"title\":\"New task\",\"projectId\":"+projectId+"}"))
      .andExpect(status().isCreated());
  }

  @Test void memberCannotCreateEvenWithForgedRoleClaim() throws Exception {
    login(Role.MEMBER);
    String token="Bearer "+jwt.generate("member@test.example","ADMIN");
    var project=projects.save(new Project("Existing","",ProjectStatus.PLANNING));
    mvc.perform(post("/api/projects").header("Authorization",token).contentType("application/json")
      .content("{\"name\":\"Forbidden\"}"))
      .andExpect(status().isForbidden()).andExpect(jsonPath("code").value("FORBIDDEN"));
    mvc.perform(post("/api/tasks").header("Authorization",token).contentType("application/json")
      .content("{\"title\":\"Forbidden\",\"projectId\":"+project.getId()+"}"))
      .andExpect(status().isForbidden());
    assertThat(projects.count()).isEqualTo(1);
    assertThat(tasks.count()).isZero();
  }
}
