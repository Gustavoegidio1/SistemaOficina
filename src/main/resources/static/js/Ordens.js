
// Listagem, cadastro/edição e detalhe das ordens de serviço.
(function (O) {
    const {
        h, money, date, today, badge, btn, acts, table, panel,
        empty, load, toast, modal, confirmDelete, form
    } = O.ui;

    // Exibe o status de pagamento com as classes visuais do CSS.
    const st = o => {
        const status = o.statusPagamento || '—';

        const classes = {
            'Pago': 'pago',
            'Parcialmente pago': 'parcial',
            'Pendente': 'pendente',
            'Não pago': 'nao-pago',
            'Vencido': 'vencido'
        };

        const classe = classes[status] || 'pendente';

        return h(
            'span',
            { class: `status-pagamento status-${classe}` },
            status
        );
    };

    // Cadastro e edição de ordens de serviço.
    async function edit(o = {}) {
        let cs = [];

        try {
            cs = await O.api.clientes.list();
        } catch (e) {
            return toast(e.message, 'bad');
        }

        if (!cs.length) {
            return toast('Cadastre um cliente antes de abrir uma ordem.', 'bad');
        }

        const f = form([
            {
                name: 'clienteId',
                label: 'Cliente',
                options: cs.map(c => [c.id, c.nome]),
                full: true
            },
            {
                name: 'placa',
                label: 'Placa',
                placeholder: 'Ex.: ABC1D23',
                required: true
            },
            {
                name: 'modelo',
                label: 'Veículo',
                placeholder: 'Ex.: Honda Civic',
                required: true
            },
            {
                name: 'quilometragem',
                label: 'Quilometragem',
                type: 'number',
                min: '0',
                placeholder: 'Ex.: 85000',
                required: true
            },
            {
                name: 'abertura',
                label: 'Data de abertura',
                type: 'date',
                def: today(),
                required: true
            }
        ], {
            clienteId: o.clienteId,
            placa: o.veiculo?.placa,
            modelo: o.veiculo?.modelo,
            quilometragem: o.quilometragem,
            abertura: o.abertura || today()
        });

        const ok = await modal({
            title: o.id
                ? `Editar ordem #${o.id}`
                : 'Nova ordem de serviço',

            body: f.node,

            onSave: async () => {
                const d = f.get();

                await O.api.ordens.save({
                    ...o,
                    clienteId: Number(d.clienteId),
                    veiculo: {
                        placa: d.placa,
                        modelo: d.modelo
                    },
                    quilometragem: Number(d.quilometragem),

                    // A data de abertura não é alterada pelo PUT atual.
                    abertura: o.id ? o.abertura : d.abertura
                });

                toast('Ordem salva.');
            }
        });

        if (ok) O.refresh();
    }

    // Exclusão de uma ordem.
    const remove = o =>
        confirmDelete(
            `Excluir a ordem #${o.id}?`,
            async () => {
                await O.api.ordens.remove(o.id);
                toast('Ordem excluída.');
            }
        ).then(ok => {
            if (ok) {
                if (location.hash.startsWith('#/ordens/')) {
                    location.hash = '#/ordens';
                } else {
                    O.refresh();
                }
            }
        });

    // Navegação para o detalhe.
    const go = id => {
        location.hash = '#/ordens/' + id;
    };

    // Identifica o tipo do item pelo formato retornado pelo Java.
    function tipoItem(i) {
        if (i.valorCobrado != null) return 'servico';
        if (i.modelo != null || i.largura != null) return 'pneu';
        if (i.categoria != null) return 'peca';

        return i.tipo || '';
    }

    // Obtém o nome do item.
    const nomeItem = i => i.nome || i.descricao || '—';

    // Obtém a quantidade.
    const quantidadeItem = i => i.quantidade ?? i.qtd ?? 0;

    // Obtém o valor unitário.
    const valorItem = i =>
        i.valorCobrado ?? i.valorVendaUnitario ?? i.valor ?? 0;

    // Calcula o subtotal.
    const subtotalItem = i =>
        quantidadeItem(i) * valorItem(i);

    // Tabela de ordens.
    O.ordensTable = os => table([
        {
            label: 'Nº',
            render: o => '#' + o.id
        },
        {
            label: 'Cliente',
            render: o => o.cliente?.nome || '—'
        },
        {
            label: 'Veículo',
            render: o => h(
                'span',
                {},
                o.veiculo?.placa || '—',
                h('span', { class: 'sub' }, o.veiculo?.modelo || '')
            )
        },
        {
            label: 'Abertura',
            render: o => date(o.abertura)
        },
        {
            label: 'Pagamento',
            render: st
        },
        {
            label: 'Total',
            cls: 'num',
            render: o => money(o.valorTotal ?? 0)
        },
        {
            label: 'Saldo',
            cls: 'num',
            render: o => money(o.saldoPendente ?? 0)
        },
        {
            label: '',
            cls: 'num',
            render: o => acts([
                ['eye', 'Ver detalhes', () => go(o.id)],
                ['edit', 'Editar', () => edit(o)],
                ['trash', 'Excluir', () => remove(o), 'bad']
            ])
        }
    ], os);

    // Tela de listagem.
    O.views.ordens = {
        title: 'Ordens de serviço',
        action: ['Nova ordem', () => edit()],

        render(box) {
            load(box, async () => {
                const rows = await O.api.ordens.list();

                return panel(
                    `${rows.length} ordem(ns)`,
                    null,
                    rows.length
                        ? O.ordensTable(rows)
                        : empty(
                            'Nenhuma ordem de serviço',
                            'Abra a primeira ordem para registrar itens e recebimentos.',
                            btn('Nova ordem', () => edit(), 'pri', 'plus')
                        )
                );
            });
        }
    };

    // Cadastro e edição de itens da ordem.
    async function editItem(os, tipo, i = {}) {
        let campos = [];

        if (tipo === 'pneu') {
            campos = [
                { name: 'nome', label: 'Nome do item', required: true, full: true },
                { name: 'quantidade', label: 'Quantidade', type: 'number', min: '1', def: 1, required: true },
                { name: 'marca', label: 'Marca', required: true },
                { name: 'valorVendaUnitario', label: 'Valor de venda unitário (R$)', type: 'number', step: '0.01', min: '0', required: true },
                { name: 'custo', label: 'Custo total (R$)', type: 'number', step: '0.01', min: '0' },
                { name: 'modelo', label: 'Modelo', required: true },
                { name: 'largura', label: 'Largura', type: 'number', min: '1', required: true },
                { name: 'perfil', label: 'Perfil', type: 'number', min: '1', required: true },
                { name: 'aro', label: 'Aro', type: 'number', min: '1', required: true }
            ];
        } else if (tipo === 'peca') {
            campos = [
                { name: 'nome', label: 'Nome da peça', required: true, full: true },
                { name: 'quantidade', label: 'Quantidade', type: 'number', min: '1', def: 1, required: true },
                { name: 'marca', label: 'Marca', required: true },
                { name: 'valorVendaUnitario', label: 'Valor de venda unitário (R$)', type: 'number', step: '0.01', min: '0', required: true },
                { name: 'custo', label: 'Custo total (R$)', type: 'number', step: '0.01', min: '0' },
                { name: 'categoria', label: 'Categoria', required: true }
            ];
        } else if (tipo === 'servico') {
            campos = [
                { name: 'nome', label: 'Nome do serviço', required: true, full: true },
                { name: 'quantidade', label: 'Quantidade', type: 'number', min: '1', def: 1, required: true },
                { name: 'valorCobrado', label: 'Valor cobrado (R$)', type: 'number', step: '0.01', min: '0', required: true }
            ];
        }

        const f = form(campos, {
            nome: i.nome ?? i.descricao ?? '',
            quantidade: quantidadeItem(i) || 1,
            marca: i.marca,
            valorVendaUnitario: i.valorVendaUnitario,
            custo: i.custo,
            modelo: i.modelo,
            largura: i.largura,
            perfil: i.perfil,
            aro: i.aro,
            categoria: i.categoria,
            valorCobrado: i.valorCobrado
        });

        const titulos = {
            pneu: 'Pneu',
            peca: 'Peça',
            servico: 'Serviço'
        };

        const ok = await modal({
            title: i.id
                ? `Editar ${titulos[tipo].toLowerCase()}`
                : `Adicionar ${titulos[tipo].toLowerCase()}`,

            body: f.node,

            onSave: async () => {
                const d = f.get();

                const dados = {
                    ...i,
                    tipo,
                    nome: d.nome,
                    quantidade: Number(d.quantidade)
                };

                if (tipo === 'pneu' || tipo === 'peca') {
                    Object.assign(dados, {
                        marca: d.marca,
                        valorVendaUnitario: Number(d.valorVendaUnitario),
                        custo: d.custo === '' || d.custo == null
                            ? null
                            : Number(d.custo)
                    });
                }

                if (tipo === 'pneu') {
                    Object.assign(dados, {
                        modelo: d.modelo,
                        largura: Number(d.largura),
                        perfil: Number(d.perfil),
                        aro: Number(d.aro)
                    });
                }

                if (tipo === 'peca') {
                    dados.categoria = d.categoria;
                }

                if (tipo === 'servico') {
                    dados.valorCobrado = Number(d.valorCobrado);
                }

                await O.api.itens.save(os.id, dados);
                toast('Item salvo.');
            }
        });

        if (ok) O.refresh();
    }

    // Exclusão de item.
    const removeItem = (os, i) =>
        confirmDelete(
            `Excluir o item "${nomeItem(i)}"?`,
            async () => {
                await O.api.itens.remove(os.id, i.id);
                toast('Item excluído.');
            }
        ).then(ok => ok && O.refresh());

    // Tela de detalhe da ordem.
    O.views.ordem = {
        title: 'Ordem de serviço',

        render(box, id) {
            load(box, async () => {
                const os = await O.api.ordens.get(id);

                const custoTotal = (os.itens || [])
                    .reduce((total, item) => {
                        if (tipoItem(item) === 'servico') return total;

                        return total + (Number(item.custo) || 0);
                    }, 0);

                const lucroBruto = (os.valorTotal || 0) - custoTotal;

                const info = (k, v) =>
                    h('div', {}, h('b', {}, k), v ?? '—');

                // Separa os itens por tipo.
                const secoes = [
                    ['pneu', 'Pneus'],
                    ['peca', 'Peças'],
                    ['servico', 'Serviços']
                ];

                const sections = secoes.map(([tipo, titulo]) => {
                    const rows = (os.itens || [])
                        .filter(i => tipoItem(i) === tipo);

                    console.log('Itens da seção:', tipo, rows);

                    return panel(
                        titulo,

                        btn(
                            'Adicionar',
                            () => editItem(os, tipo),
                            '',
                            'plus'
                        ),

                        rows.length
                            ? table([
                                {
                                    label: 'Descrição',
                                    render: nomeItem
                                },
                                {
                                    label: 'Qtd',
                                    cls: 'num',
                                    render: quantidadeItem
                                },
                                {
                                    label: 'Valor unit.',
                                    cls: 'num',
                                    render: i => money(valorItem(i))
                                },
                                {
                                    label: 'Subtotal',
                                    cls: 'num',
                                    render: i => money(subtotalItem(i))
                                },
                                {
                                    label: 'Custo total',
                                    cls: 'num',
                                    render: i => tipo === 'servico'
                                        ? '—'
                                        : i.custo == null
                                            ? 'Pendente'
                                            : money(i.custo)
                                },
                                {
                                    label: '',
                                    cls: 'num',
                                    render: i => acts([
                                        ['edit', 'Editar', () => editItem(os, tipo, i)],
                                        ['trash', 'Excluir', () => removeItem(os, i), 'bad']
                                    ])
                                }
                            ], rows)
                            : empty(
                                'Nenhum item',
                                'Use "Adicionar" para incluir.'
                            )
                    );
                });

                return h(
                    'div',
                    {},

                    h(
                        'a',
                        { class: 'back', href: '#/ordens' },
                        '← Ordens de serviço'
                    ),

                    panel(
                        `Ordem #${os.id}`,

                        h(
                            'div',
                            { class: 'acts' },
                            btn('Editar', () => edit(os), '', 'edit'),
                            btn('Excluir', () => remove(os), '', 'trash')
                        ),

                        h(
                            'div',
                            { class: 'pb info' },
                            info('Cliente', os.cliente?.nome),
                            info(
                                'Veículo',
                                `${os.veiculo?.placa || ''} ${os.veiculo?.modelo || ''}`
                            ),
                            info('Quilometragem', os.quilometragem),
                            info('Abertura', date(os.abertura)),
                            info('Pagamento', st(os)),
                            info('Valor Total', money(os.valorTotal ?? 0)),
                            info('Custo Total', money(custoTotal)),
                            info('Lucro Bruto Estimado', money(lucroBruto)),
                            info('Total Recebido', money(os.totalRecebido ?? 0)),
                            info('Saldo Pendente', money(os.saldoPendente ?? 0))
                        )
                    ),

                    sections,

                    await O.recebPanel(os)
                );
            });
        }
    };

})(window.Oficina);
