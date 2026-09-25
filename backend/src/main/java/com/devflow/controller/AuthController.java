package com.devflow.controller;

import com.devflow.dto.ApiDtos.*;
import com.devflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final AuthService auth;
  public AuthController(AuthService auth){this.auth=auth;}
  @PostMapping("/login") LoginResponse login(@Valid @RequestBody LoginRequest req){return auth.login(req);}
}
