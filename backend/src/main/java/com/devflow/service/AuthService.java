package com.devflow.service;

import com.devflow.dto.ApiDtos.*;
import com.devflow.exception.BusinessException;
import com.devflow.mapper.ApiMapper;
import com.devflow.repository.UserRepository;
import com.devflow.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
    this.users = users; this.encoder = encoder; this.jwt = jwt;
  }

  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    var user = users.findByEmail(request.email())
      .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "E-mail ou senha inválidos."));
    if (!encoder.matches(request.password(), user.getPassword())) {
      throw new BusinessException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "E-mail ou senha inválidos.");
    }
    return new LoginResponse(jwt.generate(user.getEmail(), user.getRole().name()), ApiMapper.toView(user));
  }
}
