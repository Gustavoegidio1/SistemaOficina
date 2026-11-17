
// Comunicação com a API REST (fetch).
(function (O) {
    const { config, adapters: A } = O;

    async function http(path, opt = {}) {
        let res;

        try {
            res = await fetch(config.API_BASE_URL + path, {
                headers: {
                    'Content-Type': 'application/json'
                },
                ...opt,
                body: opt.body && JSON.stringify(opt.body)
            });
        } catch {
            throw new Error(
                'Não foi possível conectar à API em ' +
                config.API_BASE_URL +
                '. Verifique se o backend está em execução e se o CORS está liberado.'
            );
        }

        if (!res.ok) {
            let msg = '';

            try {
                const texto = await res.text();

                if (texto) {
                    try {
                        const json = JSON.parse(texto);

                        msg = json.message || json.mensagem || '';

                        if (!msg && json.status === 500) {
                            msg = 'Ocorreu um erro interno no servidor. Tente novamente ou entre em contato com o suporte.';
                        }

                        if (!msg && json.status === 404) {
                            msg = 'O registro solicitado não foi encontrado.';
                        }

                        if (!msg && json.status === 400) {
                            msg = 'Os dados enviados são inválidos.';
                        }

                        if (!msg && json.status === 409) {
                            msg = 'Não foi possível concluir a operação devido a um conflito com outros dados.';
                        }

                    } catch {
                        msg = texto;
                    }
                }
            } catch {
                // Mantém a mensagem padrão abaixo.
            }

            throw new Error(
                msg || `Não foi possível concluir a operação (erro ${res.status}).`
            );
        }

        const txt = res.status === 204 ? '' : await res.text();

        return txt ? JSON.parse(txt) : null;
    }

    const list = (p, a) =>
        http(p).then(r => (r || []).map(a.in));

    const ep = n => {
        if (!config.ENDPOINTS[n]) {
            throw new Error(
                `A rota de "${n}" ainda não foi confirmada (js/config.js).`
            );
        }

        return config.ENDPOINTS[n];
    };

    const OS = config.ENDPOINTS.ordens;

    const PLURAL = {
        pneu: 'pneus',
        peca: 'pecas',
        servico: 'servicos'
    };

    const real = {
        clientes: {
            list: () => list(ep('clientes'), A.cliente),

            save: m => http(
                ep('clientes') + (m.id ? '/' + m.id : ''),
                {
                    method: m.id ? 'PUT' : 'POST',
                    body: A.cliente.out(m)
                }
            ),

            remove: id => http(
                ep('clientes') + '/' + id,
                { method: 'DELETE' }
            )
        },

        ordens: {
            list: () => list(OS, A.ordem),

            get: id =>
                http(`${OS}/${id}`).then(A.ordem.in),

            save: m => http(
                OS + (m.id ? '/' + m.id : ''),
                {
                    method: m.id ? 'PUT' : 'POST',
                    body: A.ordem.out(m)
                }
            ),

            remove: id => http(
                `${OS}/${id}`,
                { method: 'DELETE' }
            )
        },

        itens: {
            save: (o, i) => {
                const plural = PLURAL[i.tipo];

                if (!plural) {
                    throw new Error('Tipo de item inválido.');
                }

                const caminho = `${OS}/${o}/itens/${plural}`;

                return i.id
                    ? http(`${caminho}/${i.id}`, {
                        method: 'PUT',
                        body: A.item.out(i)
                    })
                    : http(caminho, {
                        method: 'POST',
                        body: A.item.out(i)
                    });
            },

            remove: (o, id) => http(
                `${OS}/${o}/itens/${id}`,
                { method: 'DELETE' }
            )
        },

        recebimentos: {
            list: o =>
                list(`${OS}/${o}/recebimentos`, A.recebimento),

            save: (o, r) => http(
                `${OS}/${o}/recebimentos` + (r.id ? '/' + r.id : ''),
                {
                    method: r.id ? 'PUT' : 'POST',
                    body: A.recebimento.out(r)
                }
            ),

            remove: (o, id) => http(
                `${OS}/${o}/recebimentos/${id}`,
                { method: 'DELETE' }
            )
        }
    };

    O.api = config.USE_DEMO ? O.demoApi : real;

})(window.Oficina);
