(function (O) {
    const { badge, btn, acts, table, panel, empty, load, toast, modal, confirmDelete, form, mask } = O.ui;

    async function edit(c = { tipo: 'PF' }) {
        const f = form([
            { name: 'tipo', label: 'Tipo de pessoa', options: [['PF', 'Pessoa física'], ['PJ', 'Pessoa jurídica']] },
            { name: 'documento', label: 'CPF', required: true },
            { name: 'nome', label: 'Nome completo', required: true, full: true },
            { name: 'razaoSocial', label: 'Razão social', full: true },
            { name: 'endereco', label: 'Endereço', required: true, full: true },
            { name: 'inscricaoEstadual', label: 'Inscrição estadual' },
            { name: 'telefone', label: 'Telefone', mask: 'telefone' },
            { name: 'email', label: 'E-mail', type: 'email' }
        ], c);

        const lab = (k, t) => {
            f.inputs[k].parentNode.firstChild.nodeValue = t;
        };

        const mostrar = (k, visivel) => {
            f.inputs[k].parentNode.style.display = visivel ? '' : 'none';
        };

        const sync = () => {
            const pj = f.inputs.tipo.value === 'PJ';

            lab('documento', pj ? 'CNPJ' : 'CPF');
            lab('nome', pj ? 'Nome de identificação' : 'Nome completo');

            // Atualiza a máscara conforme o tipo de pessoa.
            mask(f.inputs.documento, pj ? 'cnpj' : 'cpf');

            mostrar('razaoSocial', pj);
            mostrar('inscricaoEstadual', pj);

            f.inputs.razaoSocial.required = pj;
        };

        f.inputs.tipo.onchange = sync;

        // O backend não permite converter PF em PJ ou vice-versa.
        if (c.id) {
            f.inputs.tipo.disabled = true;
        }

        sync();

        if (await modal({
            title: c.id ? 'Editar cliente' : 'Novo cliente',
            body: f.node,
            onSave: async () => {
                await O.api.clientes.save({ ...c, ...f.get() });
                toast('Cliente salvo.');
            }
        })) {
            O.refresh();
        }
    }

    const remove = c =>
        confirmDelete(`Excluir o cliente "${c.nome}"?`, async () => {
            await O.api.clientes.remove(c.id);
            toast('Cliente excluído.');
        }).then(ok => ok && O.refresh());

    O.views.clientes = {
        title: 'Clientes',
        action: ['Novo cliente', () => edit()],

        render(box) {
            load(box, async () => {
                const rows = await O.api.clientes.list();

                return panel(
                    `${rows.length} cliente(s)`,
                    null,
                    rows.length
                        ? table([
                            { label: 'Nome', render: c => c.nome },
                            { label: 'Tipo', render: c => badge(c.tipo === 'PJ' ? 'Pessoa jurídica' : 'Pessoa física') },
                            { label: 'Documento', render: c => c.documento },
                            { label: 'Telefone', render: c => c.telefone },
                            { label: 'E-mail', render: c => c.email },
                            {
                                label: '',
                                cls: 'num',
                                render: c => acts([
                                    ['edit', 'Editar', () => edit(c)],
                                    ['trash', 'Excluir', () => remove(c), 'bad']
                                ])
                            }
                        ], rows)
                        : empty(
                            'Nenhum cliente cadastrado',
                            'Cadastre o primeiro cliente para abrir ordens de serviço.',
                            btn('Novo cliente', () => edit(), 'pri', 'plus')
                        )
                );
            });
        }
    };
})(window.Oficina);