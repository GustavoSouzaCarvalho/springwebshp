package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/usuarios")
public class UsuarioJPAResource {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioJPAResource(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Usuario salvarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.salvarUsuario(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isPresent()) {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    //RESETAR SENHA
    
    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<String> esqueciMinhaSenha(@RequestBody String email) {
        String token = usuarioService.gerarTokenResetSenha(email);
        // Aqui você enviaria o token ao usuário por email
        return ResponseEntity.ok("Token de redefinição de senha gerado: " + token);
    }
    
    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetarSenha(@RequestParam("token") String token, @RequestParam("novaSenha") String novaSenha) {
        usuarioService.resetarSenha(token, novaSenha);
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}