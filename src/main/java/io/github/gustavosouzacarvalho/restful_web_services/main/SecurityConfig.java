package io.github.gustavosouzacarvalho.restful_web_services.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Habilita as anotações @PreAuthorize e @PostAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desativa CSRF para facilitar testes com ferramentas como Postman
            .csrf(csrf -> csrf.disable())
            // Configura as autorizações de requisição
            .authorizeHttpRequests(auth -> auth
                // Permite acesso público ao endpoint de login
                .requestMatchers("/usuarios/login").permitAll()
                // Permite acesso público ao endpoint de criação de conta
                .requestMatchers("/usuarios/criar-conta").permitAll()
                // Permite acesso público ao console do H2 (útil durante o desenvolvimento)
                .requestMatchers("/h2-console/**").permitAll()
                // Todos os outros endpoints exigem autenticação
                .anyRequest().authenticated()
            )
            
            // Configura o uso de autenticação HTTP Basic
            .httpBasic(Customizer.withDefaults())
            // Permite o uso de frames (necessário para o console do H2)
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}