package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relacionamento com os itens do carrinho
    @OneToMany(mappedBy = "venda")
    private List<Carrinho> itensCarrinho;

    public Venda() {
    }

    public Venda(LocalDateTime data, BigDecimal total, Usuario usuario, List<Carrinho> itensCarrinho) {
        this.data = data;
        this.total = total;
        this.usuario = usuario;
        this.itensCarrinho = itensCarrinho;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Carrinho> getItensCarrinho() {
        return itensCarrinho;
    }

    public void setItensCarrinho(List<Carrinho> itensCarrinho) {
        this.itensCarrinho = itensCarrinho;
    }

	@Override
	public String toString() {
		return "Venda [id=" + id + ", data=" + data + ", total=" + total;
	}
    
    
}