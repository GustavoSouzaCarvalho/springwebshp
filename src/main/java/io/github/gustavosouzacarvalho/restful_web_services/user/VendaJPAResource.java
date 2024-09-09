package io.github.gustavosouzacarvalho.restful_web_services.user;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendas")
public class VendaJPAResource {

    private final VendaService vendaService;

    @Autowired
    public VendaJPAResource(VendaService vendaService) {
        this.vendaService = vendaService;
    }
    
    @GetMapping
    public ResponseEntity<List<Venda>> listarTodas() {
        List<Venda> vendas = vendaService.listarTodas();
        if (vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venda>> listarComprasUsuario(@PathVariable Long usuarioId) {
        List<Venda> compras = vendaService.listarComprasUsuario(usuarioId);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/usuario/{usuarioId}/data/{data}")
    public ResponseEntity<List<Venda>> comprasPorDataUsuario(@PathVariable Long usuarioId, @PathVariable String data) {
        LocalDate parsedDate = LocalDate.parse(data);
        List<Venda> compras = vendaService.comprasPorDataUsuario(usuarioId, parsedDate);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/usuario/{usuarioId}/mes/{ano}/{mes}")
    public ResponseEntity<List<Venda>> comprasPorMesUsuario(@PathVariable Long usuarioId, @PathVariable int ano, @PathVariable int mes) {
        List<Venda> compras = vendaService.comprasPorMesUsuario(usuarioId, ano, mes);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/usuario/{usuarioId}/semana-atual")
    public ResponseEntity<List<Venda>> comprasSemanaAtualUsuario(@PathVariable Long usuarioId) {
        List<Venda> compras = vendaService.comprasSemanaAtualUsuario(usuarioId);
        if (compras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compras);
    }

    @PostMapping("/usuario/{usuarioId}/realizar")
    public ResponseEntity<Venda> realizarVenda(@PathVariable Long usuarioId) {
        Venda venda = vendaService.realizarVenda(usuarioId);
        return ResponseEntity.ok(venda);
    }

    @GetMapping("/relatorio/data/{data}")
    public ResponseEntity<List<Venda>> relatorioPorData(@PathVariable String data) {
        LocalDate parsedDate = LocalDate.parse(data);
        List<Venda> vendas = vendaService.relatorioPorData(parsedDate);
        if (vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vendas);
    }
    

    @GetMapping("/relatorio/mes/{ano}/{mes}")
    public ResponseEntity<List<Venda>> relatorioPorMes(@PathVariable int ano, @PathVariable int mes) {
        List<Venda> vendas = vendaService.relatorioPorMes(ano, mes);
        if (vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/relatorio/semana-atual")
    public ResponseEntity<List<Venda>> relatorioSemanaAtual() {
        List<Venda> vendas = vendaService.relatorioSemanaAtual();
        if (vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vendas);
    }
}