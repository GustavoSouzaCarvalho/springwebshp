package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import io.github.gustavosouzacarvalho.restful_web_services.jpa.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
    
    @Cacheable("produtos")
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
    
    @Cacheable(value = "produtos", key = "#id")
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "produtos", allEntries = true)
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "produtos", allEntries = true)
    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }
    
    @Cacheable(value = "produtosPorCategoria", key = "#categoria")
    public List<Produto> buscarPorCategoria(Categoria categoria) {
        return produtoRepository.findByCategoria(categoria).stream()
                .filter(Produto::isAtivo) // Filtra produtos inativos
                .toList();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "produtos", allEntries = true)
    public Produto atualizarProduto(Produto produto) {
        // Salva o produto com as alterações feitas, como a quantidade em estoque
        return produtoRepository.save(produto);
    }
}
