package com.devflow.config;

import com.devflow.model.Role;
import com.devflow.model.User;
import com.devflow.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name="app.bootstrap.enabled",havingValue="true")
public class AdminBootstrap implements CommandLineRunner {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final String name,email,password;
  public AdminBootstrap(UserRepository users,PasswordEncoder encoder,
    @Value("${app.bootstrap.name:}") String name,
    @Value("${app.bootstrap.email:}") String email,
    @Value("${app.bootstrap.password:}") String password) {
    this.users=users;this.encoder=encoder;this.name=name;this.email=email;this.password=password;
  }
  @Override @Transactional
  public void run(String... args) {
    if(users.count()>0)return;
    if(name.isBlank()||!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")||password.length()<16||password.getBytes(StandardCharsets.UTF_8).length>72)
      throw new IllegalStateException("Configure nome, e-mail válido e senha inicial de 16 caracteres ou mais (máximo 72 bytes) para o administrador.");
    users.save(new User(name.trim(),email.trim().toLowerCase(java.util.Locale.ROOT),encoder.encode(password),Role.ADMIN));
  }
}
