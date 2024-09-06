package io.github.gustavosouzacarvalho.restful_web_services.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import io.github.gustavosouzacarvalho.restful_web_services.user.Carrinho;
import io.github.gustavosouzacarvalho.restful_web_services.user.CarrinhoService;
import io.github.gustavosouzacarvalho.restful_web_services.user.Categoria;
import io.github.gustavosouzacarvalho.restful_web_services.user.Produto;
import io.github.gustavosouzacarvalho.restful_web_services.user.ProdutoService;
import io.github.gustavosouzacarvalho.restful_web_services.user.Usuario;
import io.github.gustavosouzacarvalho.restful_web_services.user.UsuarioService;
import io.github.gustavosouzacarvalho.restful_web_services.user.Venda;
import io.github.gustavosouzacarvalho.restful_web_services.user.VendaService;

@Component
@SpringBootApplication
@EnableCaching
public class Loja implements CommandLineRunner {

    private final ProdutoService produtoService;
    private final CarrinhoService carrinhoService;
    private final VendaService vendaService;
    private final UsuarioService usuarioService;
    private final Conta conta;

    @Autowired
    public Loja(ProdutoService produtoService, CarrinhoService carrinhoService, VendaService vendaService, UsuarioService usuarioService, Conta conta) {
        this.produtoService = produtoService;
        this.carrinhoService = carrinhoService;
        this.vendaService = vendaService;
        this.usuarioService = usuarioService;
        this.conta = conta;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String token = usuarioService.obterTokenUsuarioLogado();

        while (token == null) {
            System.out.println("Usuário não autenticado. Redirecionando para o menu de contas...");
            token = conta.menuConta();
            if (token == null) {
                System.out.println("Não foi possível autenticar o usuário. Tente novamente.");
            }
        }

        System.out.println("Token recebido: " + token);
        System.out.println("Bem-vindo à Loja!");

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Ir à loja");
            System.out.println("2. Ver histórico de vendas");
            System.out.println("3. Sair");

            int decisao = scanner.nextInt();
            scanner.nextLine();

            if (decisao == 1) {
                // Menu de compras
                while (true) {
                    System.out.println("Escolha uma opção:");
                    System.out.println("1. Visualizar produtos por categoria");
                    System.out.println("2. Adicionar produto ao carrinho");
                    System.out.println("3. Remover produto do carrinho");
                    System.out.println("4. Alterar quantidade no carrinho");
                    System.out.println("5. Ver carrinho");
                    System.out.println("6. Finalizar compra");
                    System.out.println("7. Sair");

                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            exibirProdutosPorCategoria(scanner);
                            break;
                        case 2:
                            adicionarProdutoAoCarrinho(scanner, token);
                            break;
                        case 3:
                            removerProdutoDoCarrinho(scanner);
                            break;
                        case 4:
                            alterarQuantidadeCarrinho(scanner);
                            break;
                        case 5:
                            verCarrinho(token);
                            break;
                        case 6:
                            finalizarCompra(token);
                            break;
                        case 7:
                            System.out.println("Saindo da loja...");
                            return;
                        default:
                            System.out.println("Opção inválida.");
                            break;
                    }
                }
            } else if (decisao == 2) {
                verHistoricoVendas(scanner, token);
            } else if (decisao == 3) {
                System.out.println("Saindo...");
                return;
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirProdutosPorCategoria(Scanner scanner) {
        System.out.println("Categorias disponíveis:");
        for (Categoria categoria : Categoria.values()) {
            System.out.println(categoria);
        }

        System.out.print("Digite a categoria para pesquisa: ");
        String categoriaInput = scanner.nextLine();
        Categoria categoria = Categoria.valueOf(categoriaInput.toUpperCase());

        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        System.out.println("Produtos na categoria " + categoria + ":");
        for (Produto produto : produtos) {
            System.out.println(produto);
        }
    }

    private void adicionarProdutoAoCarrinho(Scanner scanner, String token) {
        System.out.print("Digite o ID do produto: ");
        Long produtoIdAdicionar = scanner.nextLong();
        System.out.print("Digite a quantidade: ");
        int quantidadeAdicionar = scanner.nextInt();
        scanner.nextLine();

        try {
            carrinhoService.adicionarAoCarrinho(produtoIdAdicionar, quantidadeAdicionar,
                    usuarioService.buscarPorEmail(usuarioService.getEmailFromToken(token)).get().getId());
            System.out.println("Produto adicionado ao carrinho.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removerProdutoDoCarrinho(Scanner scanner) {
        System.out.print("Digite o ID do produto para remover do carrinho: ");
        Long produtoIdRemover = scanner.nextLong();
        scanner.nextLine();

        carrinhoService.removerDoCarrinho(produtoIdRemover);
        System.out.println("Produto removido do carrinho.");
    }

    private void alterarQuantidadeCarrinho(Scanner scanner) {
        System.out.print("Digite o ID do produto para alterar a quantidade: ");
        Long produtoIdAlterar = scanner.nextLong();
        System.out.print("Digite a nova quantidade: ");
        int novaQuantidade = scanner.nextInt();
        scanner.nextLine();

        try {
            carrinhoService.atualizarQuantidade(produtoIdAlterar, novaQuantidade);
            System.out.println("Quantidade alterada no carrinho.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void verCarrinho(String token) {
        List<Carrinho> carrinho = carrinhoService.verCarrinhoPorToken(token);
        System.out.println("Itens no carrinho:");
        for (Carrinho item : carrinho) {
            System.out.println(item);
        }
    }

    private void finalizarCompra(String token) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(usuarioService.getEmailFromToken(token)).get();
            Venda venda = vendaService.realizarVenda(usuario.getId());
            System.out.println("Compra finalizada.");
            System.out.println("Data da venda: " + venda.getData());
            System.out.println("Total da compra: " + venda.getTotal());
        } catch (Exception e) {
            System.out.println("Erro ao finalizar a compra: " + e.getMessage());
        }
    }

    private void verHistoricoVendas(Scanner scanner, String token) {
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Vendas por dia específico");
            System.out.println("2. Vendas por mês");
            System.out.println("3. Vendas da semana atual");
            System.out.println("4. Voltar");

            int opcaoHistorico = scanner.nextInt();
            scanner.nextLine();

            switch (opcaoHistorico) {
                case 1:
                    System.out.print("Digite a data (yyyy-MM-dd): ");
                    LocalDate data = LocalDate.parse(scanner.nextLine());
                    Long usuarioIdDia = usuarioService.buscarIdPorEmail(usuarioService.getEmailFromToken(token));
                    List<Venda> vendasDia = vendaService.comprasPorDataUsuario(usuarioIdDia, data);
                    System.out.println("Vendas do dia " + data + ":");
                    for (Venda venda : vendasDia) {
                        System.out.println(venda);
                    }
                    break;
                case 2:
                    System.out.print("Digite o ano: ");
                    int ano = scanner.nextInt();
                    System.out.print("Digite o mês: ");
                    int mes = scanner.nextInt();
                    scanner.nextLine();
                    Long usuarioIdMes = usuarioService.buscarIdPorEmail(usuarioService.getEmailFromToken(token));
                    List<Venda> vendasMes = vendaService.comprasPorMesUsuario(usuarioIdMes, ano, mes);
                    System.out.println("Vendas do mês " + mes + "/" + ano + ":");
                    for (Venda venda : vendasMes) {
                        System.out.println(venda);
                    }
                    break;
                case 3:
                    Long usuarioIdSemana = usuarioService.buscarIdPorEmail(usuarioService.getEmailFromToken(token));
                    List<Venda> vendasSemana = vendaService.comprasSemanaAtualUsuario(usuarioIdSemana);
                    System.out.println("Vendas da semana atual:");
                    for (Venda venda : vendasSemana) {
                        System.out.println(venda);
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }
}