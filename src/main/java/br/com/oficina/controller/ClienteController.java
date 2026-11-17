
package br.com.oficina.controller;

import br.com.oficina.Cliente;
import br.com.oficina.PessoaFisica;
import br.com.oficina.PessoaJuridica;
import br.com.oficina.dao.ClienteDAO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public record ClienteRequest(
            String tipo,
            String nome,
            String endereco,
            String documento,
            String razaoSocial,
            String inscricaoEstadual,
            String email,
            String telefone
    ) {}

    @GetMapping
    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarClientes();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> cadastrarCliente(
            @RequestBody ClienteRequest request
    ) throws SQLException {

        Cliente cliente = montarCliente(0, request);

        if (cliente instanceof PessoaFisica pessoaFisica) {
            if (clienteDAO.cpfExiste(pessoaFisica.getCpf())) {
                throw new IllegalArgumentException("Este CPF já está cadastrado.");
            }
        }

        if (cliente instanceof PessoaJuridica pessoaJuridica) {
            if (clienteDAO.cnpjExiste(pessoaJuridica.getCnpj())) {
                throw new IllegalArgumentException("Este CNPJ já está cadastrado.");
            }
        }

        int id = clienteDAO.cadastrarCliente(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "mensagem", "Cliente cadastrado com sucesso.",
                        "id", id
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarCliente(
            @PathVariable int id,
            @RequestBody ClienteRequest request
    ) throws SQLException {

        Cliente atual = clienteDAO.buscarClientePorId(id);

        if (atual == null) {
            return ResponseEntity.notFound().build();
        }

        Cliente atualizado = montarCliente(id, request);

        // Impede transformar PF em PJ ou PJ em PF.
        if (atual.getClass() != atualizado.getClass()) {
            throw new IllegalArgumentException(
                    "Não é permitido alterar o tipo de pessoa do cliente."
            );
        }

        if (atualizado instanceof PessoaFisica novaPF) {
            PessoaFisica atualPF = (PessoaFisica) atual;

            if (!atualPF.getCpf().equals(novaPF.getCpf())
                    && clienteDAO.cpfExiste(novaPF.getCpf())) {
                throw new IllegalArgumentException("Este CPF já está cadastrado.");
            }
        }

        if (atualizado instanceof PessoaJuridica novaPJ) {
            PessoaJuridica atualPJ = (PessoaJuridica) atual;

            if (!atualPJ.getCnpj().equals(novaPJ.getCnpj())
                    && clienteDAO.cnpjExiste(novaPJ.getCnpj())) {
                throw new IllegalArgumentException("Este CNPJ já está cadastrado.");
            }
        }

        clienteDAO.atualizarCliente(atualizado);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(
            @PathVariable int id
    ) throws SQLException {

        Cliente cliente = clienteDAO.buscarClientePorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        if (clienteDAO.possuiOrdensServico(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "mensagem",
                            "Não é possível excluir este cliente porque ele possui ordens de serviço vinculadas."
                    )
            );
        }

        clienteDAO.excluirCliente(id);

        return ResponseEntity.noContent().build();
    }

    private Cliente montarCliente(int id, ClienteRequest request) {

        if (request == null || request.tipo() == null) {
            throw new IllegalArgumentException("O tipo de cliente é obrigatório.");
        }

        String tipo = request.tipo().trim().toUpperCase();

        Cliente cliente;

        if (tipo.equals("PF")) {

            cliente = new PessoaFisica(
                    id,
                    request.nome(),
                    request.endereco(),
                    request.documento()
            );

        } else if (tipo.equals("PJ")) {

            PessoaJuridica pessoaJuridica = new PessoaJuridica(
                    id,
                    request.nome(),
                    request.endereco(),
                    request.documento(),
                    request.razaoSocial()
            );

            pessoaJuridica.setInscricaoEstadual(
                    normalizarOpcional(request.inscricaoEstadual())
            );

            cliente = pessoaJuridica;

        } else {
            throw new IllegalArgumentException("Tipo de cliente inválido. Use PF ou PJ.");
        }

        cliente.setEmail(normalizarOpcional(request.email()));
        cliente.setTelefone(normalizarOpcional(request.telefone()));

        return cliente;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarErroValidacao(
            IllegalArgumentException erro
    ) {
        return ResponseEntity.badRequest().body(erro.getMessage());
    }
}
