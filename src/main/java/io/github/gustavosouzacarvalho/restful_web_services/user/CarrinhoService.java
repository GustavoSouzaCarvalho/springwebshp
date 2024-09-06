package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.gustavosouzacarvalho.restful_web_services.jpa.CarrinhoRepository;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService; // Adicione o serviço de usuário

    @Autowired
    public CarrinhoService(CarrinhoRepository carrinhoRepository, ProdutoService produtoService, UsuarioService usuarioService) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    public List<Carrinho> listarTodos() {
        return carrinhoRepository.findAll();
    }
    
    public List<Carrinho> verCarrinho(Long usuarioId) {
        return carrinhoRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Carrinho> verCarrinhoPorToken(String token) {
        String email = usuarioService.getEmailFromToken(token);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return carrinhoRepository.findByUsuarioId(usuario.getId());
        } else {
            throw new RuntimeException("Usuário não encontrado.");
        }
    }


    public Optional<Carrinho> buscarPorId(Long id) {
        return carrinhoRepository.findById(id);
    }

    public Carrinho adicionarAoCarrinho(Long produtoId, int quantidade, Long usuarioId) {
        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);

        if (produtoOpt.isPresent() && usuarioOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Usuario usuario = usuarioOpt.get();

            // Verificar se a quantidade é válida
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }

            if (quantidade > produto.getQuantidadeEmEstoque()) {
                throw new IllegalArgumentException("A quantidade solicitada excede o estoque disponível.");
            }

            // Verificar se o produto já está no carrinho para o usuário
            Optional<Carrinho> carrinhoOpt = carrinhoRepository.findByProdutoIdAndUsuarioId(produtoId, usuarioId);

            Carrinho carrinho;
            if (carrinhoOpt.isPresent()) {
                // Se o produto já está no carrinho, atualizar a quantidade
                carrinho = carrinhoOpt.get();
                int novaQuantidade = carrinho.getQuantidade() + quantidade;

                if (novaQuantidade > produto.getQuantidadeEmEstoque()) {
                    throw new IllegalArgumentException("A quantidade total no carrinho excede o estoque disponível.");
                }

                carrinho.setQuantidade(novaQuantidade);
            } else {
                // Se o produto não está no carrinho, adicionar um novo item
                carrinho = new Carrinho();
                carrinho.setProduto(produto);
                carrinho.setQuantidade(quantidade);
                carrinho.setUsuario(usuario);
            }

            return carrinhoRepository.save(carrinho);
        } else {
            throw new IllegalArgumentException("Produto ou usuário não encontrado.");
        }
    }

    public void removerDoCarrinho(Long id) {
        carrinhoRepository.deleteById(id);
    }

    public void atualizarCarrinho(Carrinho carrinho) {
        carrinhoRepository.save(carrinho);
    }

    public Carrinho atualizarQuantidade(Long id, int novaQuantidade) {
        // Verifica se a nova quantidade é válida
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Optional<Carrinho> carrinhoOpt = carrinhoRepository.findById(id); // Busca o item no carrinho pelo ID

        if (carrinhoOpt.isPresent()) {
            Carrinho carrinho = carrinhoOpt.get();
            Produto produto = carrinho.getProduto();

            // Verifica se há estoque suficiente
            if (novaQuantidade > produto.getQuantidadeEmEstoque()) {
                throw new IllegalArgumentException("Quantidade desejada excede o estoque disponível.");
            }

            // Atualiza a quantidade e salva novamente
            carrinho.setQuantidade(novaQuantidade);
            return carrinhoRepository.save(carrinho);
        } else {
            throw new RuntimeException("Item não encontrado no carrinho.");
        }
    }
}