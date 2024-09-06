package io.github.gustavosouzacarvalho.restful_web_services.jpa;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gustavosouzacarvalho.restful_web_services.user.Categoria;
import io.github.gustavosouzacarvalho.restful_web_services.user.Produto;


public interface ProdutoRepository extends JpaRepository<Produto, Long>{
	List<Produto> findByCategoria(Categoria categoria);
}
