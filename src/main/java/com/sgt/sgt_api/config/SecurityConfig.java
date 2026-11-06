package com.sgt.sgt_api.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration @EnableWebSecurity
public class SecurityConfig {
 private final JwtAuthenticationFilter jwtAuthenticationFilter; private final String allowedOrigins;
 public SecurityConfig(JwtAuthenticationFilter filter,@Value("${sgt.cors.allowed-origins}") String origins){this.jwtAuthenticationFilter=filter;this.allowedOrigins=origins;}
 @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  http.cors(cors->cors.configurationSource(corsConfigurationSource())).csrf(AbstractHttpConfigurer::disable)
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth
    .requestMatchers(HttpMethod.POST,"/api/auth/login","/api/auth/cadastrar","/api/conta/recuperar-senha","/api/conta/redefinir-senha","/api/conta/confirmar-email").permitAll()
    .requestMatchers("/swagger-ui/**","/v3/api-docs/**","/ws-sgt/**").permitAll()
    .anyRequest().authenticated())
   .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean public CorsConfigurationSource corsConfigurationSource(){
  CorsConfiguration c=new CorsConfiguration();
  c.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(s->!s.isEmpty()).toList());
  c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
  c.setAllowedHeaders(List.of("Authorization","Content-Type"));c.setAllowCredentials(true);
  UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",c);return source;
 }
}