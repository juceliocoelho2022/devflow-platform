package com.devflow.config;

import com.devflow.model.Priority;
import com.devflow.model.Project;
import com.devflow.model.ProjectStatus;
import com.devflow.model.Role;
import com.devflow.model.Task;
import com.devflow.model.TaskStatus;
import com.devflow.model.User;
import com.devflow.repository.ProjectRepository;
import com.devflow.repository.TaskRepository;
import com.devflow.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev & !prod")
public class DataSeeder {
  @Bean CommandLineRunner seed(UserRepository users,ProjectRepository projects,TaskRepository tasks,PasswordEncoder encoder){
    return args->{
      if(users.count()>0)return;
      var admin=users.save(new User("Jucelio Coelho","admin@devflow.com",encoder.encode("admin123"),Role.ADMIN));
      var maria=users.save(new User("Maria Silva","maria@devflow.com",encoder.encode("devflow123"),Role.MEMBER));
      var web=projects.save(new Project("DevFlow Web App","Plataforma principal de gestão",ProjectStatus.IN_PROGRESS));
      var mobile=projects.save(new Project("Mobile App","Aplicativo para equipes em movimento",ProjectStatus.IN_PROGRESS));
      var api=projects.save(new Project("API Gateway","Centralização das integrações",ProjectStatus.PLANNING));
      projects.save(new Project("Módulo de Relatórios","Indicadores e exportações",ProjectStatus.COMPLETED));
      String[][] items={
        {"Criar protótipo da tela de login","BACKLOG","HIGH"},{"Definir arquitetura do módulo financeiro","BACKLOG","MEDIUM"},{"Levantamento de requisitos","BACKLOG","LOW"},
        {"Implementar autenticação JWT","TODO","HIGH"},{"Criar tela de recuperação de senha","TODO","MEDIUM"},{"Configurar deploy no Docker","TODO","HIGH"},
        {"Desenvolver API de usuários","DOING","HIGH"},{"Integrar API com frontend","DOING","HIGH"},{"Implementar upload de arquivos","DOING","MEDIUM"},
        {"Revisar código de autenticação","REVIEW","HIGH"},{"Validar fluxos de permissões","REVIEW","MEDIUM"},{"Ajustes no dashboard","REVIEW","LOW"},
        {"Modelagem do banco de dados","DONE","HIGH"},{"Configuração do projeto Spring Boot","DONE","MEDIUM"},{"Criar documentação da API","DONE","LOW"}};
      for(int i=0;i<items.length;i++)tasks.save(new Task(items[i][0],"Tarefa demonstrativa do DevFlow",TaskStatus.valueOf(items[i][1]),Priority.valueOf(items[i][2]),i%4==1?mobile:i%4==2?api:web,i%2==0?admin:maria,LocalDate.now().plusDays(i+2)));
    };
  }
}
