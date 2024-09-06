package io.github.gustavosouzacarvalho.restful_web_services.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gustavosouzacarvalho.restful_web_services.user.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	 Optional<Usuario> findByEmail(String email);
}
