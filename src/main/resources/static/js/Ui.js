
// Componentes de interface reutilizáveis.
(function (O) {
    const P = {
        dash: '<rect x="3" y="3" width="7" height="9"/><rect x="14" y="3" width="7" height="5"/><rect x="14" y="12" width="7" height="9"/><rect x="3" y="16" width="7" height="5"/>',
        users: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20c0-3.6 2.9-6 6.5-6s6.5 2.4 6.5 6M16 4.5a3.5 3.5 0 0 1 0 7M18 14c2.2.6 3.5 2.4 3.5 6"/>',
        os: '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 4h6v3H9zM9 12h6M9 16h4"/>',
        cash: '<rect x="3" y="6" width="18" height="12" rx="2"/><circle cx="12" cy="12" r="2.5"/>',
        plus: '<path d="M12 5v14M5 12h14"/>',
        menu: '<path d="M4 6h16M4 12h16M4 18h16"/>',
        edit: '<path d="M4 20h4L19 9l-4-4L4 16zM13.5 6.5l4 4"/>',
        trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>',
        eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>'
    };

    const $ = (s, r = document) => r.querySelector(s);

    const h = (t, a = {}, ...k) => {
        const e = document.createElement(t);

        for (const [n, v] of Object.entries(a)) {
            if (n === 'class') {
                e.className = v;
            } else if (n.startsWith('on')) {
                e.addEventListener(n.slice(2), v);
            } else if (v != null && v !== false) {
                e.setAttribute(n, v);
            }
        }

        k.flat(9).forEach(c => {
            if (c != null && c !== false) {
                e.append(c.nodeType ? c : String(c));
            }
        });

        return e;
    };

    const icon = n => {
        const s = h('span', { class: 'ic' });
        s.innerHTML = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[n]}</svg>`;
        return s;
    };

    const money = v =>
        Number(v || 0).toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });

    const date = s =>
        s ? s.slice(0, 10).split('-').reverse().join('/') : '—';

    const today = () => new Date().toISOString().slice(0, 10);

    const STATUS = {
        ABERTA: ['Aberta', 'warn'],
        EM_ANDAMENTO: ['Em andamento', 'info'],
        CONCLUIDA: ['Concluída', 'ok']
    };

    const badge = (t, k = '') =>
        h('span', { class: 'badge ' + k }, t);

    const btn = (label, fn, cls = '', ic) =>
        h('button', { class: 'btn ' + cls, onclick: fn },
            ic && icon(ic),
            label
        );

    const acts = l =>
        h('div', { class: 'acts' },
            l.map(([i, t, fn, c]) =>
                h('button', {
                    class: 'ib ' + (c || ''),
                    title: t,
                    'aria-label': t,
                    onclick: e => {
                        e.stopPropagation();
                        fn();
                    }
                }, icon(i))
            )
        );

    const table = (cols, rows) =>
        h('div', { class: 'wrap' },
            h('table', {},
                h('thead', {},
                    h('tr', {},
                        cols.map(c =>
                            h('th', { class: c.cls }, c.label)
                        )
                    )
                ),
                h('tbody', {},
                    rows.map(r =>
                        h('tr', {},
                            cols.map(c =>
                                h('td', { class: c.cls }, c.render(r))
                            )
                        )
                    )
                )
            )
        );

    const panel = (title, action, body) =>
        h('section', { class: 'panel' },
            h('div', { class: 'ph' },
                h('h2', {}, title),
                action
            ),
            body
        );

    const calc = {
        total: o =>
            (o.itens || []).reduce(
                (s, i) => s + i.qtd * i.valor,
                0
            ),

        recebido: o =>
            (o.recebimentos || []).reduce(
                (s, r) => s + r.valor,
                0
            )
    };

    calc.saldo = o => calc.total(o) - calc.recebido(o);

    const loading = () =>
        h('div', { class: 'state' },
            h('div', { class: 'spin' }),
            'Carregando…'
        );

    const empty = (t, d, a) =>
        h('div', { class: 'state' },
            h('strong', {}, t),
            d,
            a
        );

    const error = (m, retry) =>
        h('div', { class: 'state bad' },
            h('strong', {}, 'Não foi possível carregar os dados'),
            m,
            retry && btn('Tentar novamente', retry)
        );

    async function load(box, fn) {
        box.replaceChildren(loading());

        try {
            box.replaceChildren(await fn());
        } catch (e) {
            box.replaceChildren(
                error(e.message, () => load(box, fn))
            );
        }
    }

    function toast(m, t = 'ok') {
        const e = h('div', {
            class: 'toast ' + t,
            role: 'status'
        }, m);

        $('#toasts').append(e);
        setTimeout(() => e.remove(), 3500);
    }

    // Máscaras de entrada para documentos e telefone.
    function mask(input, type) {
        input.dataset.mask = type;

        function format(value, currentType) {
            const d = value.replace(/\D/g, '');

            if (currentType === 'cpf') {
                const n = d.slice(0, 11);

                if (n.length <= 3) return n;
                if (n.length <= 6) return `${n.slice(0, 3)}.${n.slice(3)}`;
                if (n.length <= 9) return `${n.slice(0, 3)}.${n.slice(3, 6)}.${n.slice(6)}`;

                return `${n.slice(0, 3)}.${n.slice(3, 6)}.${n.slice(6, 9)}-${n.slice(9)}`;
            }

            if (currentType === 'cnpj') {
                const n = d.slice(0, 14);

                if (n.length <= 2) return n;
                if (n.length <= 5) return `${n.slice(0, 2)}.${n.slice(2)}`;
                if (n.length <= 8) return `${n.slice(0, 2)}.${n.slice(2, 5)}.${n.slice(5)}`;
                if (n.length <= 12) return `${n.slice(0, 2)}.${n.slice(2, 5)}.${n.slice(5, 8)}/${n.slice(8)}`;

                return `${n.slice(0, 2)}.${n.slice(2, 5)}.${n.slice(5, 8)}/${n.slice(8, 12)}-${n.slice(12)}`;
            }

            if (currentType === 'telefone') {
                const n = d.slice(0, 11);
                const ddd = n.slice(0, 2);
                const numero = n.slice(2);

                if (n.length <= 2) return ddd ? `(${ddd}` : '';
                if (n.length <= 6) return `(${ddd}) ${numero}`;

                const limite = n.length === 11 ? 5 : 4;

                return `(${ddd}) ${numero.slice(0, limite)}-${numero.slice(limite)}`;
            }

            return value;
        }

        // Adiciona o evento somente uma vez.
        if (!input.dataset.maskListener) {
            input.addEventListener('input', () => {
                input.value = format(input.value, input.dataset.mask);
            });

            input.dataset.maskListener = 'true';
        }

        input.value = format(input.value, type);
    }

    // Modal: onSave pode lançar erro (exibido no próprio modal).
    // Resolve true se salvou.
    function modal({ title, body, save = 'Salvar', onSave }) {
        return new Promise(res => {
            const root = $('#modal');
            const err = h('p', { class: 'err' });

            const close = v => {
                root.hidden = true;
                root.replaceChildren();
                res(v);
            };

            const ok = h('button', {
                class: 'btn pri',
                onclick: async () => {
                    err.textContent = '';
                    ok.disabled = true;
                    ok.textContent = 'Aguarde…';

                    try {
                        await onSave();
                        close(true);
                    } catch (e) {
                        err.textContent = e.message;
                        ok.disabled = false;
                        ok.textContent = save;
                    }
                }
            }, save);

            root.replaceChildren(
                h('div', {
                        class: 'dlg',
                        role: 'dialog',
                        'aria-modal': 'true'
                    },
                    h('div', { class: 'hd' },
                        h('h2', {}, title)
                    ),
                    h('div', { class: 'bd' }, body, err),
                    h('div', { class: 'ft' },
                        btn('Cancelar', () => close(false)),
                        ok
                    )
                )
            );

            root.hidden = false;

            const first = root.querySelector('input,select');
            first && first.focus();
        });
    }

    const confirmDelete = (msg, fn) =>
        modal({
            title: 'Confirmar exclusão',
            body: h('p', {}, msg),
            save: 'Excluir',
            onSave: fn
        });

    // Criação de formulários reutilizáveis.
    function form(fields, v = {}) {
        const inputs = {};

        const node = h('div', { class: 'grid' },
            fields.map(f => {
                const i = f.options
                    ? h('select', {},
                        f.options.map(([val, lab]) =>
                            h('option', { value: val }, lab)
                        )
                    )
                    : h('input', {
                        type: f.type || 'text',
                        step: f.step,
                        min: f.min,
                        placeholder: f.placeholder
                    });

                i.value = v[f.name] ?? f.def ?? '';

                if (f.mask) {
                    mask(i, f.mask);
                }

                inputs[f.name] = i;

                return h('label', {
                    class: f.full ? 'full' : ''
                }, f.label, i);
            })
        );

        const get = () => {
            const o = {};

            for (const f of fields) {
                const i = inputs[f.name];

                if (f.required && !i.value.trim()) {
                    throw new Error(
                        `Preencha o campo "${f.label}".`
                    );
                }

                o[f.name] = i.dataset.mask
                    ? i.value.replace(/\D/g, '')
                    : f.type === 'number'
                        ? Number(i.value)
                        : i.value.trim();
            }

            return o;
        };

        return { node, inputs, get };
    }

    O.ui = {
        $,
        h,
        icon,
        money,
        date,
        today,
        STATUS,
        badge,
        btn,
        acts,
        table,
        panel,
        calc,
        empty,
        load,
        toast,
        modal,
        confirmDelete,
        mask,
        form
    };

})(window.Oficina);
