package com.devflow.security;

import com.devflow.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt; private final UserRepository users;
  public JwtAuthFilter(JwtService jwt,UserRepository users){this.jwt=jwt;this.users=users;}
  @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
    var header=req.getHeader("Authorization");
    if(header!=null&&header.startsWith("Bearer ")){
      try{
        var email=jwt.subject(header.substring(7));
        users.findByEmail(email).ifPresent(user->SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(email,null,java.util.List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())))));
      }catch(Exception ignored){SecurityContextHolder.clearContext();}
    }
    chain.doFilter(req,res);
  }
}
