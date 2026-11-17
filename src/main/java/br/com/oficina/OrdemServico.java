package br.com.oficina;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdemServico {
    private int proximoId = 1;
    private int id;
    private Cliente cliente;
    private LocalDate dataAbertura;
    private String modeloVeiculo;
    private String placa;
    private int quilometragem;
    private final ArrayList<ItemOrdemServico> itensOrdemServico;
    private ArrayList<Recebimento> recebimentos;

    public OrdemServico(int id, Cliente cliente, LocalDate dataAbertura, String modeloVeiculo, String placa, int quilometragem) {
        if (id < 0) {
            throw new IllegalArgumentException("O id é inválido.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("O cliente é inválido.");
        }
        if (dataAbertura == null) {
            throw new IllegalArgumentException("A data é inválida.");
        }
        if (modeloVeiculo == null || modeloVeiculo.isBlank()) {
            throw new IllegalArgumentException("O modelo do veículo é inválido.");
        }
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("A placa é inválida.");
        }
        if (quilometragem < 0) {
            throw new IllegalArgumentException("A quilometragem é inválida.");
        }

        this.id = id;
        this.cliente = cliente;
        this.dataAbertura = dataAbertura;
        this.modeloVeiculo = modeloVeiculo;
        this.placa = placa;
        this.quilometragem = quilometragem;
        this.itensOrdemServico = new ArrayList<>();
        this.recebimentos = new ArrayList<>();
    }

    private void validarNovoItem(ItemOrdemServico item) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }

        if (item.getId() != 0) {
            throw new IllegalArgumentException("Este item já foi adicionado.");
        }
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID é inválido.");
        }

        if (this.id != 0) {
            throw new IllegalArgumentException("A ordem de serviço já possui um ID.");
        }

        this.id = id;
    }

    public void adicionarPneu(Pneu pneu) {
        validarNovoItem(pneu);
        adicionarItemOrdemServico(pneu);
    }


    public void adicionarPeca(Peca peca) {
        validarNovoItem(peca);
        adicionarItemOrdemServico(peca);
    }

    public void adicionarServico(Servico servico) {
        validarNovoItem(servico);
        adicionarItemOrdemServico(servico);
    }

    private void adicionarItemOrdemServico(ItemOrdemServico item) {
        item.setId(proximoId);
        proximoId++;
        itensOrdemServico.add(item);
    }

    public List<Recebimento> getRecebimentos() {
        return List.copyOf(recebimentos);
    }

    public void adicionarRecebimento(Recebimento recebimento) {
        if (recebimento == null) {
            throw new IllegalArgumentException("Recebimento inválido.");
        }

        recebimentos.add(recebimento);
    }


    public double getTotalRecebido() {
        double total = 0;

        for (Recebimento recebimento : recebimentos) {
            if (recebimento.isPago()) {
                total += recebimento.getValor();
            }
        }

        return arredondarCentavos(total);
    }

    public double getSaldoPendente() {
        return arredondarCentavos(getValorTotal() - getTotalRecebido());
    }

    private double arredondarCentavos(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public List<ItemOrdemServico> getItensOrdemServico() {
        return List.copyOf(itensOrdemServico);
    }

    public double getValorTotal() {
        double total = 0;

        for (ItemOrdemServico item : itensOrdemServico) {
            total += item.valorTotal();
        }

        return arredondarCentavos(total);
    }

    public String getStatusPagamento() {
        if (getTotalRecebido() == 0) {
            return "Não pago";
        }

        if (getTotalRecebido() >= getValorTotal()) {
            return "Pago";
        }

        return "Parcialmente pago";
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public String getModeloVeiculo() {
        return modeloVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public int getQuilometragem() {
        return quilometragem;
    }

    public Pneu buscarPneuPorId(int id) {
        for (ItemOrdemServico item : itensOrdemServico) {
            if (item instanceof Pneu) {
                Pneu pneu = (Pneu) item;

                if (pneu.getId() == id) {
                    return pneu;
                }
            }
        }
        return null;
    }

    public Peca buscarPecaPorId(int id) {
        for (ItemOrdemServico item : itensOrdemServico) {
            if (item instanceof Peca) {
                Peca peca = (Peca) item;

                if (peca.getId() == id) {
                    return peca;
                }
            }
        }
        return null;
    }

    public Produto buscarProdutoPorId(int id) {
        for (ItemOrdemServico item : itensOrdemServico) {
            if (item instanceof Produto) {
                Produto produto = (Produto) item;

                if (produto.getId() == id) {
                    return produto;
                }
            }
        }
        return null;
    }

    public Servico buscarServicoPorId(int id) {
        for (ItemOrdemServico item : itensOrdemServico) {
            if (item instanceof Servico) {
                Servico servico = (Servico) item;

                if (servico.getId() == id) {
                    return servico;
                }
            }
        }
        return null;
    }

    public ItemOrdemServico buscarItemPorId(int id) {
        for (ItemOrdemServico item : itensOrdemServico) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void alterarCliente(Cliente novoCliente) {
        if (novoCliente == null) {
            throw new IllegalArgumentException("O cliente é inválido.");
        }

        this.cliente = novoCliente;
    }

    public void alterarModeloVeiculo(String novoModelo) {
        if (novoModelo == null || novoModelo.isBlank()) {
            throw new IllegalArgumentException("O modelo do veículo é inválido.");
        }

        this.modeloVeiculo = novoModelo;
    }

    public void alterarPlaca(String novaPlaca) {
        if (novaPlaca == null || novaPlaca.isBlank()) {
            throw new IllegalArgumentException("A placa é inválida.");
        }

        this.placa = novaPlaca;
    }

    public void alterarQuilometragem(int novaQuilometragem) {
        if (novaQuilometragem < 0) {
            throw new IllegalArgumentException("A quilometragem é inválida.");
        }

        this.quilometragem = novaQuilometragem;
    }

    public boolean alterarValorItem(int id, double novoValor) {
        ItemOrdemServico item = buscarItemPorId(id);
        if (item == null) {
            return false;
        }

        item.alterarValor(novoValor);
        return true;
    }

    public boolean alterarNomeItem(int id, String novoNome) {
        ItemOrdemServico item = buscarItemPorId(id);
        if (item == null) {
            return false;
        }

        item.alterarNome(novoNome);
        return true;
    }

    public boolean alterarQuantidade(int id, int novaQuantidade) {
        ItemOrdemServico item = buscarItemPorId(id);

        if (item == null) {
            return false;
        }

        item.alterarQuantidade(novaQuantidade);
        return true;
    }

    public boolean alterarMarca(int id, String novaMarca) {
        Produto produto = buscarProdutoPorId(id);
        if (produto == null) {
            return false;
        }

        produto.alterarMarca(novaMarca);
        return true;
    }

    public boolean alterarModelo(int id, String novoModelo) {
        Pneu pneu = buscarPneuPorId(id);
        if (pneu == null) {
            return false;
        }

        pneu.alterarModelo(novoModelo);
        return true;
    }

    public boolean alterarLargura(int id, int novaLargura) {
        Pneu pneu = buscarPneuPorId(id);
        if (pneu == null) {
            return false;
        }

        pneu.alterarLargura(novaLargura);
        return true;
    }

    public boolean alterarPerfil(int id, int novoPerfil) {
        Pneu pneu = buscarPneuPorId(id);
        if (pneu == null) {
            return false;
        }

        pneu.alterarPerfil(novoPerfil);
        return true;
    }

    public boolean alterarAro(int id, int novoAro) {
        Pneu pneu = buscarPneuPorId(id);
        if (pneu == null) {
            return false;
        }

        pneu.alterarAro(novoAro);
        return true;
    }

    public boolean alterarCategoria(int id, String categoria) {
        Peca peca = buscarPecaPorId(id);
        if (peca == null) {
            return false;
        }

        peca.alterarCategoria(categoria);
        return true;
    }


    public boolean removerItemPorId(int id) {
        ItemOrdemServico item = buscarItemPorId(id);

        if (item == null) {
            return false;
        }

        return itensOrdemServico.remove(item);
    }

}
