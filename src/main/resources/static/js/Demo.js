// Dados DEMONSTRATIVOS em memória. Não representam dados reais e somem ao recarregar a página.
(function (O) {
    let seq = 100;
    const db = {
        clientes: [
            { id: 1, tipo: 'PF', nome: 'Cliente Demonstrativo A', documento: '000.000.000-00', telefone: '(00) 00000-0001', email: 'demo.a@exemplo.com' },
            { id: 2, tipo: 'PJ', nome: 'Empresa Demonstrativa B', documento: '00.000.000/0001-00', telefone: '(00) 00000-0002', email: 'demo.b@exemplo.com' }
        ],
        ordens: [
            { id: 1, clienteId: 1, cliente: { id: 1, nome: 'Cliente Demonstrativo A' }, veiculo: { placa: 'DEM-0001', modelo: 'Veículo demonstrativo 1' }, abertura: '2026-11-03', status: 'EM_ANDAMENTO',
                itens: [{ id: 11, tipo: 'pneu', descricao: 'Pneu demonstrativo', qtd: 4, valor: 380 }, { id: 12, tipo: 'servico', descricao: 'Serviço demonstrativo', qtd: 1, valor: 160 }, { id: 13, tipo: 'peca', descricao: 'Peça demonstrativa', qtd: 4, valor: 12 }],
                recebimentos: [{ id: 21, valor: 800, data: '2026-11-03', forma: 'Pix' }] },
            { id: 2, clienteId: 2, cliente: { id: 2, nome: 'Empresa Demonstrativa B' }, veiculo: { placa: 'DEM-0002', modelo: 'Veículo demonstrativo 2' }, abertura: '2026-11-08', status: 'CONCLUIDA',
                itens: [{ id: 14, tipo: 'servico', descricao: 'Serviço demonstrativo', qtd: 2, valor: 250 }],
                recebimentos: [{ id: 22, valor: 500, data: '2026-11-09', forma: 'Boleto' }] }
        ]
    };
    const wait = v => new Promise(r => setTimeout(() => r(structuredClone(v)), 300));
    const os = i => db.ordens.find(o => o.id == i);
    const up = (arr, m) => { const x = m.id && arr.find(a => a.id == m.id); if (x) Object.assign(x, m); else arr.push({ ...m, id: ++seq }); return wait(null); };
    const del = (arr, id) => { arr.splice(arr.findIndex(a => a.id == id), 1); return wait(null); };
    O.demoApi = {
        clientes: { list: () => wait(db.clientes), save: m => up(db.clientes, m), remove: id => del(db.clientes, id) },
        ordens: {
            list: () => wait(db.ordens), get: id => wait(os(id)),
            save: m => { m.cliente = db.clientes.find(c => c.id == m.clienteId); return up(db.ordens, { itens: [], recebimentos: [], ...m }); },
            remove: id => del(db.ordens, id)
        },
        itens: { save: (o, i) => up(os(o).itens, i), remove: (o, id) => del(os(o).itens, id) },
        recebimentos: { list: o => wait(os(o).recebimentos), save: (o, r) => up(os(o).recebimentos, r), remove: (o, id) => del(os(o).recebimentos, id) }
    };
})(window.Oficina);