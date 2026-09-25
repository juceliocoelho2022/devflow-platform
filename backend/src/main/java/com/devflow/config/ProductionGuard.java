package com.devflow.config;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionGuard {
  public ProductionGuard(Environment environment) {
    if(Arrays.asList(environment.getActiveProfiles()).contains("dev"))
      throw new IllegalStateException("Não combine os perfis dev e prod.");
    String secret=environment.getProperty("app.jwt.secret","");
    if(secret.isBlank()||secret.getBytes(StandardCharsets.UTF_8).length<32||Set.of(
      "devflow-enterprise-local-secret-key-2026","change-this-secret-with-at-least-32-characters").contains(secret))
      throw new IllegalStateException("Configure JWT_SECRET com uma chave aleatória própria de pelo menos 32 bytes.");
    String password=environment.getProperty("spring.datasource.password","");
    if(password.isBlank()||password.equals("devflow"))
      throw new IllegalStateException("Configure uma senha própria para o banco de produção.");
    String origins=environment.getProperty("app.cors-origin","");
    for(String origin:origins.split(",",-1)) {
      try {
        URI uri=URI.create(origin.trim());
        if(!"https".equals(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null||
          uri.getQuery()!=null||uri.getFragment()!=null||!uri.getPath().isEmpty()||origin.contains("*"))
          throw new IllegalArgumentException();
      }catch(IllegalArgumentException exception){
        throw new IllegalStateException("CORS_ORIGIN deve conter origens HTTPS explícitas, sem caminho ou curingas.");
      }
    }
  }
}
