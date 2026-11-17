
(function (O) {

    const cliente = {

        // Converte o objeto recebido do Java para o formato do frontend.
        in: r => ({
            id: r.id,
            tipo: r.cpf != null ? 'PF' : 'PJ',
            documento: r.cpf ?? r.cnpj ?? '',
            nome: r.nome ?? '',
            endereco: r.endereco ?? '',
            razaoSocial: r.razaoSocial ?? '',
            inscricaoEstadual: r.inscricaoEstadual ?? '',
            email: r.email ?? '',
            telefone: r.telefone ?? ''
        }),

        // Converte o formulário para o formato esperado pelo ClienteController.
        out: m => {
            const dados = {
                tipo: m.tipo,
                nome: m.nome,
                endereco: m.endereco,
                email: m.email || null,
                telefone: m.telefone || null,
                documento: m.documento
            };

            if (m.tipo === 'PJ') {
                dados.razaoSocial = m.razaoSocial;
                dados.inscricaoEstadual = m.inscricaoEstadual || null;
            }

            return dados;
        }
    };


    const ordem = {

        // Java -> frontend
        in: r => ({
            id: r.id,
            clienteId: r.cliente?.id,
            cliente: r.cliente,

            veiculo: {
                modelo: r.modeloVeiculo,
                placa: r.placa
            },

            abertura: r.dataAbertura,
            quilometragem: r.quilometragem,
            itens: r.itensOrdemServico || [],
            recebimentos: r.recebimentos || [],

            status: 'ABERTA',
            valorTotal: r.valorTotal,
            totalRecebido: r.totalRecebido,
            saldoPendente: r.saldoPendente,
            statusPagamento: r.statusPagamento
        }),

        // frontend -> Java
        out: m => ({
            idCliente: Number(m.clienteId),
            dataAbertura: m.abertura,
            modeloVeiculo: m.veiculo.modelo,
            placa: m.veiculo.placa,
            quilometragem: Number(m.quilometragem)
        })
    };


    const item = {

        // Java -> frontend
        in: r => ({
            ...r,

            tipo: r.valorCobrado != null
                ? 'servico'
                : r.modelo != null || r.largura != null
                    ? 'pneu'
                    : 'peca'
        }),

        // frontend -> Java
        out: m => {

            const base = {
                nome: m.nome,
                quantidade: Number(m.quantidade)
            };

            if (m.tipo === 'servico') {
                return {
                    ...base,
                    valorCobrado: Number(m.valorCobrado)
                };
            }

            const produto = {
                ...base,
                marca: m.marca,
                valorVendaUnitario: Number(m.valorVendaUnitario),
                custo: m.custo === '' || m.custo == null
                    ? null
                    : Number(m.custo)
            };

            if (m.tipo === 'pneu') {
                return {
                    ...produto,
                    modelo: m.modelo,
                    largura: Number(m.largura),
                    perfil: Number(m.perfil),
                    aro: Number(m.aro)
                };
            }

            if (m.tipo === 'peca') {
                return {
                    ...produto,
                    categoria: m.categoria
                };
            }

            throw new Error('Tipo de item inválido.');
        }
    };



    const recebimento = {

        // Java -> frontend
        in: r => ({
            ...r,
            dataPrevista: r.dataPrevista,
            dataRecebimento: r.dataRecebimento ?? null,
            formaPagamento: r.formaPagamento,
            status: r.status
        }),

        // frontend -> Java
        out: m => ({
            valor: Number(m.valor),
            dataPrevista: m.dataPrevista,
            dataRecebimento: m.dataRecebimento || null,
            formaPagamento: m.formaPagamento
        })
    };



    O.adapters = {
        cliente,
        ordem,
        item,
        recebimento
    };

})(window.Oficina);
