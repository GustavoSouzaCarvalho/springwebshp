package io.github.gustavosouzacarvalho.restful_web_services.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gustavosouzacarvalho.restful_web_services.user.Carrinho;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long>{
	 Optional<Carrinho> findByProdutoId(Long produtoId);
	 Optional<Carrinho> findByProdutoIdAndUsuarioId(Long produtoId, Long usuarioId);
	 List<Carrinho> findByUsuarioId(Long usuarioId);
}
