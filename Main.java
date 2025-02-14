import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Pessoa {
    String nome;
    String sexo;
    int idade;
    int quantidadePessoas;

    public Pessoa(String nome, String sexo, int idade, int quantidadePessoas) {
        this.nome = nome;
        this.sexo = sexo;
        this.idade = idade;
        this.quantidadePessoas = quantidadePessoas;
    }

    public boolean podeReservar() {
        return idade >= 18;
    }
}

class Quarto {
    int numero;
    String tipo;
    double preco;
    int capacidade;
    boolean ocupado;
    Pessoa pessoaResponsavel;

    public Quarto(int numero, String tipo, double preco, int capacidade) {
        this.numero = numero;
        this.tipo = tipo;
        this.preco = preco;
        this.capacidade = capacidade;
        this.ocupado = false;
        this.pessoaResponsavel = null;
    }

    public boolean reservar(Pessoa pessoa) {
        if (!ocupado && pessoa.quantidadePessoas <= capacidade) {
            ocupado = true;
            pessoaResponsavel = pessoa;
            return true;
        }
        return false;
    }

    public void liberar() {
        ocupado = false;
        pessoaResponsavel = null;
    }
}

class Hotel {
    List<Quarto> quartos;
    List<Pessoa> pessoas;

    public Hotel() {
        quartos = new ArrayList<>();
        pessoas = new ArrayList<>();

        // Criando os quartos do hotel
        for (int i = 1; i <= 60; i++) {
            quartos.add(new Quarto(i, "Simples", 50.00, 2));
        }
        for (int i = 61; i <= 90; i++) {
            quartos.add(new Quarto(i, "Comum", 60.00, 4));
        }
        for (int i = 91; i <= 100; i++) {
            quartos.add(new Quarto(i, "Luxuoso", 120.00, 8));
        }
    }

    public boolean fazerReserva(Pessoa pessoa) {
        for (Quarto quarto : quartos) {
            if (quarto.reservar(pessoa)) {
                pessoas.add(pessoa);
                return true;
            }
        }
        return false;
    }

    public void cancelarReserva(Pessoa pessoa) {
        for (Quarto quarto : quartos) {
            if (quarto.pessoaResponsavel == pessoa) {
                quarto.liberar();
                pessoas.remove(pessoa);
                break;
            }
        }
    }

    public double calcularMediaPreco() {
        double total = 0;
        for (Quarto quarto : quartos) {
            if (quarto.ocupado) {
                total += quarto.preco;
            }
        }
        return total / quartos.size();
    }

    public double calcularMediaPrecoPorTipo(String tipo) {
        double total = 0;
        int count = 0;
        for (Quarto quarto : quartos) {
            if (quarto.ocupado && quarto.tipo.equals(tipo)) {
                total += quarto.preco;
                count++;
            }
        }
        return count > 0 ? total / count : 0;
    }

    public double calcularMediaPrecoPorSexo(String sexo) {
        double total = 0;
        int count = 0;
        for (Quarto quarto : quartos) {
            if (quarto.ocupado && quarto.pessoaResponsavel.sexo.equals(sexo)) {
                total += quarto.preco;
                count++;
            }
        }
        return count > 0 ? total / count : 0;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hotel hotel = new Hotel();

        while (true) {
            System.out.println("1. Fazer reserva");
            System.out.println("2. Cancelar reserva");
            System.out.println("3. Calcular média de preços");
            System.out.println("4. Calcular média por tipo de quarto");
            System.out.println("5. Calcular média por sexo");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();  // Consome a quebra de linha

            if (opcao == 1) {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Sexo (Masculino, Feminino, Outro): ");
                String sexo = scanner.nextLine();
                System.out.print("Idade: ");
                int idade = scanner.nextInt();
                System.out.print("Quantas pessoas: ");
                int quantidadePessoas = scanner.nextInt();
                scanner.nextLine();  // Consome a quebra de linha

                Pessoa pessoa = new Pessoa(nome, sexo, idade, quantidadePessoas);
                if (pessoa.podeReservar()) {
                    if (hotel.fazerReserva(pessoa)) {
                        System.out.println("Reserva feita com sucesso!");
                    } else {
                        System.out.println("Não há quartos disponíveis para essa quantidade de pessoas.");
                    }
                } else {
                    System.out.println("A pessoa deve ter pelo menos 18 anos para fazer uma reserva.");
                }
            } else if (opcao == 2) {
                System.out.print("Nome da pessoa para cancelar reserva: ");
                String nome = scanner.nextLine();
                Pessoa pessoa = null;
                for (Pessoa p : hotel.pessoas) {
                    if (p.nome.equals(nome)) {
                        pessoa = p;
                        break;
                    }
                }
                if (pessoa != null) {
                    hotel.cancelarReserva(pessoa);
                    System.out.println("Reserva cancelada com sucesso!");
                } else {
                    System.out.println("Pessoa não encontrada.");
                }
            } else if (opcao == 3) {
                double mediaPreco = hotel.calcularMediaPreco();
                System.out.println("Média do preço de todas as reservas: R$" + mediaPreco);
            } else if (opcao == 4) {
                System.out.print("Digite o tipo de quarto (Simples, Comum, Luxuoso): ");
                String tipo = scanner.nextLine();
                double mediaPrecoTipo = hotel.calcularMediaPrecoPorTipo(tipo);
                System.out.println("Média do preço dos quartos " + tipo + ": R$" + mediaPrecoTipo);
            } else if (opcao == 5) {
                System.out.print("Digite o sexo da pessoa (Masculino, Feminino, Outro): ");
                String sexo = scanner.nextLine();
                double mediaPrecoSexo = hotel.calcularMediaPrecoPorSexo(sexo);
                System.out.println("Média do preço dos quartos para pessoas " + sexo + ": R$" + mediaPrecoSexo);
            } else if (opcao == 6) {
                break;
            } else {
                System.out.println("Opção inválida.");
            }
        }
        scanner.close();
    }
}
