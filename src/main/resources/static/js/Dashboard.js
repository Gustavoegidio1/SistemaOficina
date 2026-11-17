
// Os indicadores são calculados a partir dos dados reais da API.
(function (O) {
    const { h, money, panel, empty, load } = O.ui;

    O.views.dashboard = {
        title: 'Dashboard',

        render(box) {
            load(box, async () => {
                const os = await O.api.ordens.list();

                const total = os.reduce(
                    (s, o) => s + Number(o.valorTotal || 0), 0
                );

                const rec = os.reduce(
                    (s, o) => s + Number(o.totalRecebido || 0), 0
                );

                const pendente = os.reduce(
                    (s, o) => s + Number(o.saldoPendente || 0), 0
                );

                const custo = os.reduce((totalCusto, o) => {
                    const custoOrdem = (o.itens || []).reduce(
                        (s, item) => {
                            if (item.valorCobrado != null) return s;

                            return s + (Number(item.custo) || 0);
                        },
                        0
                    );

                    return totalCusto + custoOrdem;
                }, 0);

                const lucro = total - custo;

                const pct = total
                    ? Math.round(rec / total * 100)
                    : 0;

                const card = (k, v) =>
                    h('div', { class: 'card' },
                        h('div', { class: 'k' }, k),
                        h('div', { class: 'v' }, v)
                    );

                return h('div', {},
                    h('div', { class: 'cards' },
                        card('Ordens cadastradas', os.length),
                        card('Valor Total', money(total)),
                        card('Valor Recebido', money(rec)),
                        card('Valor Pendente', money(pendente)),
                        card('Custo Total', money(custo)),
                        card('Lucro Bruto estimado', money(lucro))
                    ),

                    panel(
                        'Recebido do total',
                        h('span', { class: 'muted' }, pct + '%'),
                        h('div', { class: 'bar' },
                            h('i', { style: `width:${pct}%` })
                        )
                    ),

                    panel(
                        'Últimas ordens',
                        null,
                        os.length
                            ? O.ordensTable(os.slice(0, 5))
                            : empty(
                                'Nenhuma ordem de serviço',
                                'Os indicadores aparecem após a primeira ordem.'
                            )
                    )
                );
            });
        }
    };

})(window.Oficina);
