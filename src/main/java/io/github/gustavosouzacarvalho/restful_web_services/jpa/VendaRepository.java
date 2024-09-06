package io.github.gustavosouzacarvalho.restful_web_services.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.gustavosouzacarvalho.restful_web_services.user.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long>{
    List<Venda> findByUsuarioId(Long usuarioId); // Buscar vendas por usuário

    // Vendas por uma data específica
    List<Venda> findByDataBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT v FROM Venda v WHERE YEAR(v.data) = :year AND MONTH(v.data) = :month")
    List<Venda> findByDataYearAndDataMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT v FROM Venda v WHERE v.data BETWEEN :startOfWeek AND :endOfWeek")
    List<Venda> findByDataBetweenWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);
    
    // Vendas de um usuário específico em uma data específica
    List<Venda> findByUsuarioIdAndDataBetween(Long usuarioId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT v FROM Venda v WHERE v.usuario.id = :usuarioId AND YEAR(v.data) = :year AND MONTH(v.data) = :month")
    List<Venda> findByUsuarioIdAndDataYearAndDataMonth(
            @Param("usuarioId") Long usuarioId, 
            @Param("year") int year, 
            @Param("month") int month);

    // Vendas de um usuário na semana atual
    @Query("SELECT v FROM Venda v WHERE v.usuario.id = :usuarioId AND v.data BETWEEN :startOfWeek AND :endOfWeek")
    List<Venda> findByUsuarioIdAndDataBetweenWeek(
            @Param("usuarioId") Long usuarioId, 
            @Param("startOfWeek") LocalDateTime startOfWeek, 
            @Param("endOfWeek") LocalDateTime endOfWeek);
}
