package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/usuarios")
public class UsuarioJPAResource {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioJPAResource(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
    // ATUALIZAR USUÁRIO
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setSenha(usuarioAtualizado.getSenha());
            usuario.setPapel(usuarioAtualizado.getPapel());
            Usuario usuarioAtualizadoResponse = usuarioService.salvarUsuario(usuario);
            return ResponseEntity.ok(usuarioAtualizadoResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    
    // AUTENTICAÇÃO
    @GetMapping("/me")
    public ResponseEntity<String> obterUsuarioLogado() {
        String emailUsuarioLogado = usuarioService.obterTokenUsuarioLogado();
        if (emailUsuarioLogado != null) {
            return ResponseEntity.ok("Usuário logado: " + emailUsuarioLogado);
        } else {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }
    }
    
    
    @PostMapping("/criar-conta")
    public ResponseEntity<String> criarConta(@RequestBody Usuario usuario) {
        // Verifica se o email já está cadastrado
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário cadastrado com este email.");
        }

        usuario.setPapel(Papel.USUARIO);

        // Salva o novo usuário
        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok("Conta criada com sucesso para usuário: " + usuarioSalvo.getNome());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/criar-conta-admin")
    public ResponseEntity<String> criarContaAdmin(@RequestBody Usuario usuario) {
        // Verifica se o email já está cadastrado
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário cadastrado com este email.");
        }
        usuario.setPapel(Papel.ADMIN);
        // Salva o novo usuário
        Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok("Conta ADMIN criada com sucesso para: " + usuarioSalvo.getNome());
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(usuario.getEmail());
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
        
        Usuario user = usuarioOpt.get();
        
        if (!passwordEncoder.matches(usuario.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
        
        String token = user.gerarTokenJWT(usuarioService.obterTokenUsuarioLogado(), 3600000);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(token);
    }
    
    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<String> esqueciMinhaSenha(@RequestBody String email) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
        if (usuarioOpt.isPresent()) {
            // Token para reset de senha
            String token = usuarioService.gerarTokenResetSenha(email);
            return ResponseEntity.ok("Token de redefinição de senha gerado e enviado para o email: " + email);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email não encontrado.");
        }
    }
    
    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetarSenha(@RequestParam("token") String token, @RequestParam("novaSenha") String novaSenha) {
        try {

            usuarioService.resetarSenha(token, novaSenha);
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
}