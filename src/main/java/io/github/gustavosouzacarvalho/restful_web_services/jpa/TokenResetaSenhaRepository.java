package io.github.gustavosouzacarvalho.restful_web_services.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gustavosouzacarvalho.restful_web_services.user.TokenResetaSenha;


public interface TokenResetaSenhaRepository extends JpaRepository<TokenResetaSenha, Long> {
    Optional<TokenResetaSenha> findByToken(String token);
}