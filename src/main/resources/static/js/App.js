// Roteador por hash (#/dashboard, #/ordens/5, ...) e estrutura da página.
(function (O) {
    const { $, h, icon, btn } = O.ui;
    const NAV = [['dashboard', 'Dashboard', 'dash'], ['clientes', 'Clientes', 'users'], ['ordens', 'Ordens de serviço', 'os'], ['recebimentos', 'Recebimentos', 'cash']];
    $('#nav').append(...NAV.map(([k, l, i]) => h('a', { href: '#/' + k, 'data-k': k }, icon(i), l)));
    $('#menu').append(icon('menu'));
    $('#menu').onclick = () => $('#side').classList.toggle('open');
    if (O.config.USE_DEMO) $('#banner').append(h('div', { class: 'demo' }, 'Modo demonstrativo: os dados exibidos são fictícios e não vêm da API.'));

    function route() {
        const [, name = 'dashboard', arg] = location.hash.slice(1).split('/');
        const key = name === 'ordens' && arg ? 'ordem' : name;
        const view = O.views[key] || O.views.dashboard;
        document.querySelectorAll('#nav a').forEach(a => a.classList.toggle('on', a.dataset.k === (O.views[name] ? name : 'dashboard')));
        $('#title').textContent = view.title;
        $('#actions').replaceChildren(...(view.action ? [btn(view.action[0], view.action[1], 'pri', 'plus')] : []));
        const box = h('div', {}); $('#view').replaceChildren(box);
        $('#side').classList.remove('open');
        view.render(box, arg);
    }
    O.refresh = route;
    addEventListener('hashchange', route);
    route();
})(window.Oficina);