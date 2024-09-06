package io.github.gustavosouzacarvalho.restful_web_services.user;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.github.gustavosouzacarvalho.restful_web_services.jpa.TokenResetaSenhaRepository;
import io.github.gustavosouzacarvalho.restful_web_services.jpa.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UsuarioService {

	@Value("${jwt.secret}")
	private String secretKey;

	private final UsuarioRepository usuarioRepository;
	private final TokenResetaSenhaRepository tokenResetaSenhaRepository;

	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository, TokenResetaSenhaRepository tokenResetaSenhaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.tokenResetaSenhaRepository = tokenResetaSenhaRepository;
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	public Usuario salvarUsuario(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public void deletarUsuario(Long id) {
		usuarioRepository.deleteById(id);
	}

	public Optional<Usuario> buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	// TOKEN
	public String gerarTokenResetSenha(String email) {
	    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
	    if (usuarioOpt.isEmpty()) {
	        throw new RuntimeException("Usuário não encontrado com este email.");
	    }

	    Usuario usuario = usuarioOpt.get();
	    // Remove tokens antigos antes de criar um novo
	    tokenResetaSenhaRepository.deleteByUsuario(usuario);

	    String token = UUID.randomUUID().toString();
	    LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token válido por 1 hora

	    TokenResetaSenha resetaSenha = new TokenResetaSenha(token, usuario, expiryDate);
	    tokenResetaSenhaRepository.save(resetaSenha);

	    return token; // Token enviado ao usuário
	}

	public void resetarSenha(String token, String novaSenha) {
		Optional<TokenResetaSenha> tokenOpt = tokenResetaSenhaRepository.findByToken(token);
		if (tokenOpt.isEmpty() || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token inválido ou expirado.");
		}

		Usuario usuario = tokenOpt.get().getUsuario();
		usuario.setSenha(novaSenha);
		usuarioRepository.save(usuario);

		// Remove o token após o uso
		tokenResetaSenhaRepository.delete(tokenOpt.get());
	}

	public String gerarToken(String email) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + 36000000); // Data de expiração para 10 horas a partir da
																// criação

		return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	public String getEmailFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public Long buscarIdPorEmail(String email) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
		if (usuarioOpt.isPresent()) {
			return usuarioOpt.get().getId();
		} else {
			throw new RuntimeException("Usuário não encontrado com o email: " + email);
		}
	}
	
	public String obterTokenUsuarioLogado() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    
	    if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
	        // Caso em que não há um usuário autenticado
	        return null;
	    }
	    
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    return userDetails.getUsername(); // ou outra lógica para retornar o e-mail ou token
	}

}
   


