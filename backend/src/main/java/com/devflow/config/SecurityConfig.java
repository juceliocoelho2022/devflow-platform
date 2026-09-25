package com.devflow.config;

import com.devflow.security.JwtAuthFilter;
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
    c.setAllowedOrigins(List.of(origins.split(",")));c.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);
    var source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
  }
  @Bean SecurityFilterChain filter(HttpSecurity http,JwtAuthFilter jwt)throws Exception{
    return http.csrf(c->c.disable()).cors(c->{})
      .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html","/actuator/health").permitAll().anyRequest().authenticated())
      .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();
  }
}
