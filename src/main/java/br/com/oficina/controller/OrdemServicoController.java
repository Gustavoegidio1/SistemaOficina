package br.com.oficina.controller;

import br.com.oficina.Cliente;
import br.com.oficina.OrdemServico;
import br.com.oficina.Pneu;
import br.com.oficina.Peca;
import br.com.oficina.Servico;
import br.com.oficina.Recebimento;
import br.com.oficina.ItemOrdemServico;


import br.com.oficina.dao.ClienteDAO;
import br.com.oficina.dao.OrdemServicoDAO;
import br.com.oficina.dao.RecebimentoDAO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private double calcularTotalRecebimentosPendentes(OrdemServico ordem) {
        return ordem.getRecebimentos()
                .stream()
                .filter(r -> !r.isPago())
                .mapToDouble(Recebimento::getValor)
                .sum();
    }

    // DTO para receber os dados de uma OS
    public record OrdemServicoRequest(
            int idCliente,
            LocalDate dataAbertura,
            String modeloVeiculo,
            String placa,
            int quilometragem
    ) {}

    public record PneuRequest(
            String nome,
            int quantidade,
            String marca,
            double valorVendaUnitario,
            Double custo,
            String modelo,
            int largura,
            int perfil,
            int aro
    ) {}

    public record PecaRequest(
            String nome,
            int quantidade,
            String marca,
            double valorVendaUnitario,
            Double custo,
            String categoria
    ) {}

    public record ServicoRequest(
            String nome,
            int quantidade,
            double valorCobrado
    ) {}

    public record RecebimentoRequest(
            double valor,
            LocalDate dataPrevista,
            LocalDate dataRecebimento,
            String formaPagamento
    ) {}

    // Buscar OS por ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscarPorId(@PathVariable int id) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ordem);
    }

    // Cadastrar OS
    @PostMapping
    public ResponseEntity<Map<String, Object>> cadastrar(
            @RequestBody OrdemServicoRequest request) throws SQLException {

        Cliente cliente = clienteDAO.buscarClientePorId(request.idCliente());

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cliente não encontrado."
            );
        }

        OrdemServico ordem = new OrdemServico(
                0,
                cliente,
                request.dataAbertura(),
                request.modeloVeiculo(),
                request.placa(),
                request.quilometragem()
        );

        ordemServicoDAO.cadastrarOrdemServico(ordem);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensagem", "Ordem de serviço cadastrada.",
                        "id", ordem.getId()
                ));
    }

    // Excluir OS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException {

        boolean removida = ordemServicoDAO.removerOrdemServico(id);

        if (!removida) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable int id,
            @RequestBody OrdemServicoRequest request) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        Cliente cliente = clienteDAO.buscarClientePorId(request.idCliente());

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cliente não encontrado."
            );
        }

        ordem.alterarCliente(cliente);
        ordem.alterarModeloVeiculo(request.modeloVeiculo());
        ordem.alterarPlaca(request.placa());
        ordem.alterarQuilometragem(request.quilometragem());

        ordemServicoDAO.atualizarOrdemServico(ordem);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/itens/pneus")
    public ResponseEntity<Map<String, Object>> adicionarPneu(
            @PathVariable int id,
            @RequestBody PneuRequest request) throws SQLException {

        // Busca a ordem de serviço
        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        // Cria o objeto Pneu usando os dados recebidos
        Pneu pneu = new Pneu(
                request.nome(),
                request.quantidade(),
                request.marca(),
                request.valorVendaUnitario(),
                request.modelo(),
                request.largura(),
                request.perfil(),
                request.aro()
        );

        // Define o custo, caso tenha sido informado
        if (request.custo() != null) {
            pneu.setCusto(request.custo());
        }

        // Salva o pneu no banco de dados
        ordemServicoDAO.adicionarItemOrdemServico(ordem, pneu);

        // Retorna o ID gerado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensagem", "Pneu adicionado à ordem de serviço.",
                        "id", pneu.getId()
                ));
    }

    @PutMapping("/{id}/itens/pneus/{idItem}")
    public ResponseEntity<Void> atualizarPneu(
            @PathVariable int id,
            @PathVariable int idItem,
            @RequestBody PneuRequest request) throws SQLException {

        // Busca a ordem de serviço
        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        // Busca o item pelo ID
        var item = ordem.buscarItemPorId(idItem);

        if (!(item instanceof Pneu pneu)) {
            return ResponseEntity.notFound().build();
        }

        // Altera os dados do pneu
        pneu.alterarNome(request.nome());
        pneu.alterarQuantidade(request.quantidade());
        pneu.alterarMarca(request.marca());
        pneu.alterarValor(request.valorVendaUnitario());
        pneu.alterarModelo(request.modelo());
        pneu.alterarLargura(request.largura());
        pneu.alterarPerfil(request.perfil());
        pneu.alterarAro(request.aro());

        if (request.custo() != null) {
            pneu.setCusto(request.custo());
        } else {
            pneu.limparCusto();
        }

        // Atualiza no banco de dados
        ordemServicoDAO.atualizarItemOrdemServico(ordem, pneu);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/itens/pecas")
    public ResponseEntity<Map<String, Object>> adicionarPeca(
            @PathVariable int id,
            @RequestBody PecaRequest request) throws SQLException {

        // Busca a ordem de serviço
        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        // Cria o objeto Peca
        Peca peca = new Peca(
                request.nome(),
                request.quantidade(),
                request.marca(),
                request.valorVendaUnitario(),
                request.categoria()
        );

        // Define o custo, caso informado
        if (request.custo() != null) {
            peca.setCusto(request.custo());
        }

        // Salva no banco de dados
        ordemServicoDAO.adicionarItemOrdemServico(ordem, peca);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensagem", "Peça adicionada à ordem de serviço.",
                        "id", peca.getId()
                ));
    }

    @PutMapping("/{id}/itens/pecas/{idItem}")
    public ResponseEntity<Void> atualizarPeca(
            @PathVariable int id,
            @PathVariable int idItem,
            @RequestBody PecaRequest request) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        var item = ordem.buscarItemPorId(idItem);

        if (!(item instanceof Peca peca)) {
            return ResponseEntity.notFound().build();
        }

        peca.alterarNome(request.nome());
        peca.alterarQuantidade(request.quantidade());
        peca.alterarMarca(request.marca());
        peca.alterarValor(request.valorVendaUnitario());
        peca.alterarCategoria(request.categoria());

        if (request.custo() != null) {
            peca.setCusto(request.custo());
        } else {
            peca.limparCusto();
        }

        ordemServicoDAO.atualizarItemOrdemServico(ordem, peca);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/itens/servicos")
    public ResponseEntity<Map<String, Object>> adicionarServico(
            @PathVariable int id,
            @RequestBody ServicoRequest request) throws SQLException {

        // Busca a ordem de serviço
        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        // Cria o objeto Servico
        Servico servico = new Servico(
                request.nome(),
                request.quantidade(),
                request.valorCobrado()
        );

        // Salva no banco de dados
        ordemServicoDAO.adicionarItemOrdemServico(ordem, servico);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "mensagem", "Serviço adicionado à ordem de serviço.",
                        "id", servico.getId()
                ));
    }

    @PutMapping("/{id}/itens/servicos/{idItem}")
    public ResponseEntity<Void> atualizarServico(
            @PathVariable int id,
            @PathVariable int idItem,
            @RequestBody ServicoRequest request) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        var item = ordem.buscarItemPorId(idItem);

        if (!(item instanceof Servico servico)) {
            return ResponseEntity.notFound().build();
        }

        servico.alterarNome(request.nome());
        servico.alterarQuantidade(request.quantidade());
        servico.alterarValor(request.valorCobrado());

        ordemServicoDAO.atualizarItemOrdemServico(ordem, servico);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/itens/{idItem}")
    public ResponseEntity<Void> removerItem(
            @PathVariable int id,
            @PathVariable int idItem) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        ItemOrdemServico item = ordem.buscarItemPorId(idItem);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        double novoTotal = ordem.getValorTotal() - item.valorTotal();

        if (novoTotal < ordem.getTotalRecebido()) {
            throw new IllegalArgumentException(
                    "Não é possível remover este item, pois o valor total da OS ficaria menor que o total já recebido."
            );
        }

        boolean removido = ordemServicoDAO.removerItemOrdemServico(id, idItem);

        if (!removido) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/recebimentos")
    public ResponseEntity<?> cadastrarRecebimento(
            @PathVariable int id,
            @RequestBody RecebimentoRequest request
    ) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        Recebimento recebimento = new Recebimento(
                0,
                request.valor(),
                request.dataPrevista(),
                request.dataRecebimento(),
                request.formaPagamento()
        );

        double saldoDisponivel = ordem.getSaldoPendente()
                - calcularTotalRecebimentosPendentes(ordem);

        if (recebimento.getValor() > saldoDisponivel) {
            return ResponseEntity.badRequest().body(
                    "O recebimento ultrapassa o saldo disponível, considerando os recebimentos pendentes."
            );
        }

        RecebimentoDAO recebimentoDAO = new RecebimentoDAO();

        recebimentoDAO.cadastrarRecebimento(id, recebimento);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recebimento.getId());
    }

    @PutMapping("/{id}/recebimentos/{idRecebimento}")
    public ResponseEntity<?> atualizarRecebimento(
            @PathVariable int id,
            @PathVariable int idRecebimento,
            @RequestBody RecebimentoRequest request
    ) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        Recebimento recebimentoAntigo = ordem.getRecebimentos()
                .stream()
                .filter(r -> r.getId() == idRecebimento)
                .findFirst()
                .orElse(null);

        if (recebimentoAntigo == null) {
            return ResponseEntity.notFound().build();
        }

        double saldoDisponivel = ordem.getSaldoPendente()
                - calcularTotalRecebimentosPendentes(ordem)
                + recebimentoAntigo.getValor();

        if (request.valor() > saldoDisponivel) {
            return ResponseEntity.badRequest().body(
                    "O recebimento não pode ser maior que o saldo pendente."
            );
        }

        Recebimento recebimento = new Recebimento(
                idRecebimento,
                request.valor(),
                request.dataPrevista(),
                request.dataRecebimento(),
                request.formaPagamento()
        );

        RecebimentoDAO recebimentoDAO = new RecebimentoDAO();

        boolean atualizado = recebimentoDAO.atualizarRecebimento(id, recebimento);

        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/recebimentos/{idRecebimento}")
    public ResponseEntity<Void> removerRecebimento(
            @PathVariable int id,
            @PathVariable int idRecebimento
    ) throws SQLException {

        RecebimentoDAO recebimentoDAO = new RecebimentoDAO();

        boolean removido = recebimentoDAO.removerRecebimento(id, idRecebimento);

        if (!removido) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarErroDeValidacao(
            IllegalArgumentException erro
    ) {
        return ResponseEntity.badRequest().body(erro.getMessage());
    }

    @GetMapping("/{id}/recebimentos")
    public ResponseEntity<?> buscarRecebimentos(@PathVariable int id) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ordem.getRecebimentos());
    }

    @GetMapping("/{id}/recebimentos/{idRecebimento}")
    public ResponseEntity<?> buscarRecebimento(
            @PathVariable int id,
            @PathVariable int idRecebimento
    ) throws SQLException {

        OrdemServico ordem = ordemServicoDAO.buscarOrdemServicoPorId(id);

        if (ordem == null) {
            return ResponseEntity.notFound().build();
        }

        Recebimento recebimento = ordem.getRecebimentos()
                .stream()
                .filter(r -> r.getId() == idRecebimento)
                .findFirst()
                .orElse(null);

        if (recebimento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(recebimento);
    }


    @GetMapping
    public List<OrdemServico> listarOrdensServico() throws SQLException {
        return ordemServicoDAO.listarOrdensServico();
    }

}