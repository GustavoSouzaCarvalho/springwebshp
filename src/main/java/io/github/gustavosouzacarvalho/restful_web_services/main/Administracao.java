package io.github.gustavosouzacarvalho.restful_web_services.main;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.gustavosouzacarvalho.restful_web_services.user.Categoria;
import io.github.gustavosouzacarvalho.restful_web_services.user.Papel;
import io.github.gustavosouzacarvalho.restful_web_services.user.Produto;
import io.github.gustavosouzacarvalho.restful_web_services.user.ProdutoService;
import io.github.gustavosouzacarvalho.restful_web_services.user.Usuario;
import io.github.gustavosouzacarvalho.restful_web_services.user.UsuarioService;

@Component
public class Administracao {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @Autowired
    public Administracao(ProdutoService produtoService, UsuarioService usuarioService) {
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    public void menuAdministracao(Usuario usuarioLogado) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Criar produto");
            System.out.println("2. Ler produtos");
            System.out.println("3. Atualizar produto");
            System.out.println("4. Excluir produto");
            System.out.println("5. Criar usuário ADMIN");
            System.out.println("6. Voltar");

            int escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1:
                    criarProduto(scanner);
                    break;
                case 2:
                    lerProdutos();
                    break;
                case 3:
                    atualizarProduto(scanner);
                    break;
                case 4:
                    excluirProduto(scanner);
                    break;
                case 5:
                    if (usuarioLogado.getPapel() == Papel.ADMIN) {
                        criarUsuarioAdmin(scanner);
                    } else {
                        System.out.println("Você não tem permissão para criar outros ADMINs.");
                    }
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private void criarProduto(Scanner scanner) {
        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();

        System.out.print("Descrição do produto: ");
        String descricao = scanner.nextLine();

        System.out.print("Preço do produto: ");
        BigDecimal preco = scanner.nextBigDecimal();
        scanner.nextLine();

        System.out.print("Quantidade em estoque: ");
        int quantidadeEmEstoque = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Categoria do produto (ROUPA, ELETRONICO, MOBILIARIO, ELETRODOMESTICO, FERRAMENTA, CALÇADO, BRINQUEDO, LIVRO, ACESSORIO): ");
        String categoriaInput = scanner.nextLine();
        Categoria categoria = Categoria.valueOf(categoriaInput.toUpperCase());

        Produto produto = new Produto(nome, descricao, preco, categoria, quantidadeEmEstoque, true);
        produtoService.salvarProduto(produto);

        System.out.println("Produto criado com sucesso!");
    }

    private void lerProdutos() {
        List<Produto> produtos = produtoService.listarTodos();
        System.out.println("Produtos cadastrados:");
        for (Produto produto : produtos) {
            System.out.println(produto);
        }
    }

    private void atualizarProduto(Scanner scanner) {
        System.out.print("Digite o ID do produto a ser atualizado: ");
        Long produtoId = scanner.nextLong();
        scanner.nextLine();

        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();

            System.out.print("Novo nome do produto (deixe em branco para não alterar): ");
            String nome = scanner.nextLine();
            if (!nome.isEmpty()) {
                produto.setNome(nome);
            }

            System.out.print("Nova descrição do produto (deixe em branco para não alterar): ");
            String descricao = scanner.nextLine();
            if (!descricao.isEmpty()) {
                produto.setDescricao(descricao);
            }

            System.out.print("Novo preço do produto (deixe em branco para não alterar): ");
            String precoInput = scanner.nextLine();
            if (!precoInput.isEmpty()) {
                BigDecimal preco = new BigDecimal(precoInput);
                produto.setPreco(preco);
            }

            System.out.print("Nova quantidade em estoque (deixe em branco para não alterar): ");
            String quantidadeInput = scanner.nextLine();
            if (!quantidadeInput.isEmpty()) {
                int quantidade = Integer.parseInt(quantidadeInput);
                produto.setQuantidadeEmEstoque(quantidade);
            }

            System.out.print("Nova categoria do produto (deixe em branco para não alterar): ");
            String categoriaInput = scanner.nextLine();
            if (!categoriaInput.isEmpty()) {
                Categoria categoria = Categoria.valueOf(categoriaInput.toUpperCase());
                produto.setCategoria(categoria);
            }

            produtoService.atualizarProduto(produto);
            System.out.println("Produto atualizado com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private void excluirProduto(Scanner scanner) {
        System.out.print("Digite o ID do produto a ser excluído: ");
        Long produtoId = scanner.nextLong();
        scanner.nextLine();

        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        if (produtoOpt.isPresent()) {
            produtoService.deletarProduto(produtoId);
            System.out.println("Produto excluído com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private void criarUsuarioAdmin(Scanner scanner) {
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
        usuario.setPapel(Papel.ADMIN);

        usuarioService.salvarUsuario(usuario);
        System.out.println("Usuário ADMIN cadastrado com sucesso!");
    }
}