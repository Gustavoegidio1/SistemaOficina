
// Tela de Recebimentos + painel reutilizado na ordem de serviço.
(function (O) {
    const {
        h, money, date, today, btn, acts, table, panel,
        calc, empty, load, toast, modal, confirmDelete, form
    } = O.ui;

    const FORMAS = [
        'Dinheiro',
        'Pix',
        'Cartão de débito',
        'Cartão de crédito',
        'Boleto',
        'Transferência'
    ];

    async function edit(os, r = {}) {
        const f = form([
            {
                name: 'valor',
                label: 'Valor (R$)',
                type: 'number',
                step: '0.01',
                min: '0.01',
                required: true
            },
            {
                name: 'dataPrevista',
                label: 'Data prevista para pagamento',
                type: 'date',
                def: today(),
                required: true
            },
            {
                name: 'formaPagamento',
                label: 'Forma de pagamento',
                options: FORMAS.map(x => [x, x]),
                full: true
            },
            {
                name: 'dataRecebimento',
                label: 'Data em que o pagamento foi recebido',
                type: 'date',
                full: true
            }
        ], r);

        if (await modal({
            title: r.id ? 'Editar recebimento' : 'Novo recebimento',
            body: f.node,
            onSave: async () => {
                const dados = f.get();

                dados.dataRecebimento = dados.dataRecebimento || null;

                await O.api.recebimentos.save(os.id, {
                    ...r,
                    ...dados
                });

                toast('Recebimento salvo.');
            }
        })) {
            O.refresh();
        }
    }

    const remove = (os, r) =>
        confirmDelete(
            `Excluir o recebimento de ${money(r.valor)}?`,
            async () => {
                await O.api.recebimentos.remove(os.id, r.id);
                toast('Recebimento excluído.');
            }
        ).then(ok => ok && O.refresh());

    const summary = (os, rec) => {
        const t = Number(os.valorTotal ?? calc.total(os));
        const r = Number(os.totalRecebido ?? 0);
        const s = Number(os.saldoPendente ?? (t - r));

        const card = (k, v) =>
            h(
                'div',
                { class: 'card' },
                h('div', { class: 'k' }, k),
                h('div', { class: 'v' }, money(v))
            );

        return h(
            'div',
            { class: 'cards' },
            card('Total da ordem', t),
            card('Recebido', r),
            card('Saldo pendente', s)
        );
    };

    const statusPagamento = r => {
        const status = r.status ||
            (r.dataRecebimento ? 'Pago' : 'Pendente');

        const classes = {
            'Pago': 'pago',
            'Parcialmente pago': 'parcial',
            'Pendente': 'pendente',
            'Vencido': 'vencido'
        };

        const classe = classes[status] || 'pendente';

        return h(
            'span',
            { class: `status-pagamento status-${classe}` },
            status
        );
    };

    O.recebPanel = async os => {
        const rec = await O.api.recebimentos.list(os.id);

        const body = rec.length ? table([
            {
                label: 'Data Prevista',
                render: r => date(r.dataPrevista)
            },
            {
                label: 'Data Recebida',
                render: r => r.dataRecebimento
                    ? date(r.dataRecebimento)
                    : '—'
            },
            {
                label: 'Forma de Pagamento',
                render: r => r.formaPagamento
            },
            {
                label: 'Situação',
                render: statusPagamento
            },
            {
                label: 'Valor',
                cls: 'num',
                render: r => money(r.valor)
            },
            {
                label: '',
                cls: 'num',
                render: r => acts([
                    ['edit', 'Editar', () => edit(os, r)],
                    ['trash', 'Excluir', () => remove(os, r), 'bad']
                ])
            }
        ], rec) : empty(
            'Nenhum recebimento registrado',
            'Registre o primeiro pagamento desta ordem.'
        );

        return h(
            'div',
            {},
            summary(os, rec),
            panel(
                'Recebimentos',
                btn('Novo recebimento', () => edit(os), 'pri', 'plus'),
                body
            )
        );
    };

    O.views.recebimentos = {
        title: 'Recebimentos',

        render(box, id) {
            load(box, async () => {
                const list = await O.api.ordens.list();

                const sel = h(
                    'select',
                    {
                        onchange: e => {
                            location.hash = '#/recebimentos' +
                                (e.target.value ? '/' + e.target.value : '');
                        }
                    },
                    h('option', { value: '' }, 'Selecione uma ordem de serviço'),
                    list.map(o => h(
                        'option',
                        { value: o.id },
                        `#${o.id} — ${o.cliente?.nome || 'Cliente'} (${o.veiculo?.placa || 'sem placa'})`
                    ))
                );

                sel.value = id || '';

                const out = h('div', {});

                if (id) {
                    out.append(await O.recebPanel(await O.api.ordens.get(id)));
                } else {
                    out.append(empty(
                        'Escolha uma ordem de serviço',
                        'Os recebimentos são listados por ordem.'
                    ));
                }

                return h(
                    'div',
                    {},
                    h(
                        'div',
                        { class: 'panel pb' },
                        h('label', {}, 'Ordem de serviço', sel)
                    ),
                    out
                );
            });
        }
    };
})(window.Oficina);
