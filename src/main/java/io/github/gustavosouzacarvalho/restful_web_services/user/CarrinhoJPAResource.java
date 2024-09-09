package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoJPAResource {

    private final CarrinhoService carrinhoService;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @Autowired
    public CarrinhoJPAResource(CarrinhoService carrinhoService, ProdutoService produtoService, UsuarioService usuarioService) {
        this.carrinhoService = carrinhoService;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Carrinho>> listarTodos() {
        List<Carrinho> carrinhos = carrinhoService.listarTodos();
        return ResponseEntity.ok(carrinhos);
    }

    // Buscar um item no carrinho por ID
    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarPorId(@PathVariable Long id) {
        Optional<Carrinho> carrinhoOpt = carrinhoService.buscarPorId(id);
        return carrinhoOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/token")
    public ResponseEntity<List<Carrinho>> verCarrinhoPorToken(@RequestParam String token) {
        try {
            List<Carrinho> carrinhos = carrinhoService.verCarrinhoPorToken(token);
            return ResponseEntity.ok(carrinhos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Adicionar um novo item ao carrinho
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarAoCarrinho(@RequestParam Long produtoId, @RequestParam int quantidade, @RequestParam Long usuarioId) {
        try {
            // Validação da quantidade
            if (quantidade <= 0) {
                return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
            }

            Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
            if (produtoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Produto não encontrado.");
            }

            Produto produto = produtoOpt.get();
            if (quantidade > produto.getQuantidadeEmEstoque()) {
                return ResponseEntity.badRequest().body("Quantidade excede o estoque disponível.");
            }

            Carrinho carrinho = carrinhoService.adicionarAoCarrinho(produtoId, quantidade, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Produto adicionado ao carrinho com sucesso. ID do Carrinho: " + carrinho.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Atualizar a quantidade de um item no carrinho
    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarQuantidade(@PathVariable Long id, @RequestBody int novaQuantidade) {
        try {
            // Validação da nova quantidade
            if (novaQuantidade <= 0) {
                return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
            }

            Optional<Carrinho> carrinhoOpt = carrinhoService.buscarPorId(id);
            if (carrinhoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Carrinho carrinho = carrinhoOpt.get();
            Produto produto = carrinho.getProduto();
            if (novaQuantidade > produto.getQuantidadeEmEstoque()) {
                return ResponseEntity.badRequest().body("Quantidade desejada excede o estoque disponível.");
            }

            carrinhoService.atualizarQuantidade(id, novaQuantidade);
            return ResponseEntity.ok("Quantidade atualizada com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Remover um item do carrinho
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        Optional<Carrinho> carrinhoOpt = carrinhoService.buscarPorId(id);
        if (carrinhoOpt.isPresent()) {
            carrinhoService.removerDoCarrinho(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
