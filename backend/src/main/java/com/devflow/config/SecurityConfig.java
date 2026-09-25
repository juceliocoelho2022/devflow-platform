package com.devflow.config;

import com.devflow.security.JwtAuthFilter;
import com.devflow.dto.ApiDtos.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpMethod;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
  @Bean CorsConfigurationSource cors(@Value("${app.cors-origin}") String origins){
    var c=new CorsConfiguration();
    c.setAllowedOrigins(java.util.Arrays.stream(origins.split(",")).map(String::trim).toList());c.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);
    var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
  }
  @Bean SecurityFilterChain filter(HttpSecurity http,JwtAuthFilter jwt,ObjectMapper mapper)throws Exception{
    return http.csrf(c->c.disable()).cors(c->{})
      .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(errors->errors
        .authenticationEntryPoint((req,res,error)->{
          res.setStatus(401);res.setContentType("application/json");res.setCharacterEncoding("UTF-8");
          mapper.writeValue(res.getWriter(),new ApiError(401,"UNAUTHORIZED","Sua sessão expirou ou é inválida. Entre novamente.",LocalDateTime.now(),Map.of()));
        })
        .accessDeniedHandler((req,res,error)->{
          res.setStatus(403);res.setContentType("application/json");res.setCharacterEncoding("UTF-8");
          mapper.writeValue(res.getWriter(),new ApiError(403,"FORBIDDEN","Você não tem permissão para realizar esta ação.",LocalDateTime.now(),Map.of()));
        }))
      .authorizeHttpRequests(a->a
        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
        .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
        .requestMatchers(HttpMethod.POST,"/api/projects","/api/tasks").hasAnyRole("ADMIN","MANAGER")
        .requestMatchers(HttpMethod.GET,"/api/projects","/api/tasks","/api/dashboard").hasAnyRole("ADMIN","MANAGER","MEMBER")
        .requestMatchers(HttpMethod.PATCH,"/api/tasks/*/status").hasAnyRole("ADMIN","MANAGER","MEMBER")
        .anyRequest().denyAll())
      .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();
  }
}
