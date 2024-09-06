package io.github.gustavosouzacarvalho.restful_web_services.main;

import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.gustavosouzacarvalho.restful_web_services.user.Papel;
import io.github.gustavosouzacarvalho.restful_web_services.user.Usuario;
import io.github.gustavosouzacarvalho.restful_web_services.user.UsuarioService;

@Component
public class Conta {

    private final UsuarioService usuarioService;
    private final Administracao administracao;

    @Autowired
    public Conta(UsuarioService usuarioService, Administracao administracao) {
        this.usuarioService = usuarioService;
        this.administracao = administracao;
    }

    public String menuConta() {
        Scanner scanner = new Scanner(System.in);
        String token = null;

        while (token == null) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Fazer login");
            System.out.println("2. Criar conta");
            System.out.println("3. Esqueci minha senha");

            int escolha = scanner.nextInt();	
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    token = login(scanner);
                    break;
                case 2:
                    criarConta(scanner);
                    break;
                case 3:
                    esqueciMinhaSenha(scanner);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }

            if (token == null) {
                System.out.println("Retornando ao menu principal...");
            }
        }
        return token;
    }

    private String login(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            String token = usuarioService.gerarToken(email);
            System.out.println("Login realizado com sucesso!");
            System.out.println("Token gerado: " + token);
            Papel papel = usuarioOpt.get().getPapel();
            if (papel == Papel.ADMIN) {
                administracao.menuAdministracao(usuarioOpt.get()); // Redireciona para a administração
            } else {
                System.out.println("Você está logado como USUARIO.");
                // Adicione aqui o menu para usuários comuns, se necessário
            }
            
            return token;
        } else {
            System.out.println("Email ou senha inválidos.");
            return null;
        }
    }

    private void criarConta(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setPapel(Papel.USUARIO);

        usuarioService.salvarUsuario(usuario);
        System.out.println("Usuário cadastrado com sucesso!");

    }

    private void esqueciMinhaSenha(Scanner scanner) {
        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
        if (usuarioOpt.isPresent()) {
            String token = usuarioService.gerarTokenResetSenha(email);
            System.out.println("Token de reset de senha gerado: " + token);
            System.out.println("Por favor, use o token enviado para redefinir sua senha.");
            
            System.out.print("Digite o token recebido: ");
            String tokenRecebido = scanner.nextLine();

            if (tokenRecebido.equals(token)) {
                System.out.print("Digite a nova senha: ");
                String novaSenha = scanner.nextLine();
                usuarioService.resetarSenha(tokenRecebido, novaSenha);
                System.out.println("Senha redefinida com sucesso!");
            } else {
                System.out.println("Token inválido.");
            }
        } else {
            System.out.println("Email não encontrado.");
        }
    }
}