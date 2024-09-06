package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.gustavosouzacarvalho.restful_web_services.jpa.CarrinhoRepository;
import io.github.gustavosouzacarvalho.restful_web_services.jpa.VendaRepository;
import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoService produtoService;

    @Autowired
    public VendaService(VendaRepository vendaRepository, CarrinhoRepository carrinhoRepository, ProdutoService produtoService) {
        this.vendaRepository = vendaRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.produtoService = produtoService;	
    }
    
    @Cacheable("venda")
    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }
    
    @Cacheable("vendas")
    public List<Venda> listarComprasUsuario(Long usuarioId) {
        return vendaRepository.findByUsuarioId(usuarioId);
    }
    
    @CacheEvict(value = "vendas", allEntries = true)
    public List<Venda> comprasPorDataUsuario(Long usuarioId, LocalDate data) {
        LocalDateTime start = data.atStartOfDay();
        LocalDateTime end = data.atTime(LocalTime.MAX);
        return vendaRepository.findByUsuarioIdAndDataBetween(usuarioId, start, end);
    }

    // Compras de um usuário por mês
    public List<Venda> comprasPorMesUsuario(Long usuarioId, int ano, int mes) {
        return vendaRepository.findByUsuarioIdAndDataYearAndDataMonth(usuarioId, ano, mes);
    }
    
    // Compras de um usuário na semana atual
    public List<Venda> comprasSemanaAtualUsuario(Long usuarioId) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
        return vendaRepository.findByUsuarioIdAndDataBetweenWeek(usuarioId, startOfWeek, endOfWeek);
    }

    @Transactional
    @CacheEvict(value = "vendas", allEntries = true)
    public Venda realizarVenda(Long usuarioId) {
        List<Carrinho> itensCarrinho = carrinhoRepository.findByUsuarioId(usuarioId);

        if (itensCarrinho.isEmpty()) {
            throw new IllegalArgumentException("Carrinho está vazio.");
        }

        BigDecimal total = calcularTotal(itensCarrinho);

        // Atualizar o estoque de cada produto vendido
        for (Carrinho item : itensCarrinho) {
            Produto produto = item.getProduto();
            int novaQuantidade = produto.getQuantidadeEmEstoque() - item.getQuantidade();
            if (novaQuantidade < 0) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setQuantidadeEmEstoque(novaQuantidade);
            produtoService.atualizarProduto(produto);
        }

        // Criar uma nova venda
        Venda venda = new Venda(LocalDateTime.now(), total, itensCarrinho.get(0).getUsuario(), itensCarrinho);
        vendaRepository.save(venda);

        // Limpar o carrinho após a compra
        carrinhoRepository.deleteAll(itensCarrinho);

        return venda;
    }

    private BigDecimal calcularTotal(List<Carrinho> itensCarrinho) {
        return itensCarrinho.stream()
                .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Relatório por data informada
    public List<Venda> relatorioPorData(LocalDate data) {
        LocalDateTime start = data.atStartOfDay();
        LocalDateTime end = data.atTime(LocalTime.MAX);
        return vendaRepository.findByDataBetween(start, end);
    }
    
    public List<Venda> relatorioPorMes(int ano, int mes) {
        return vendaRepository.findByDataYearAndDataMonth(ano, mes);
    }
    
    // Relatório da semana atual
    public List<Venda> relatorioSemanaAtual() {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
        return vendaRepository.findByDataBetweenWeek(startOfWeek, endOfWeek);
    }
}	