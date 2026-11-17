
// Configuração central da aplicação.
window.Oficina = {
    views: {},

    config: {
        API_BASE_URL: 'http://localhost:8080/api',

        USE_DEMO: false,

        ENDPOINTS: {
            ordens: '/ordens-servico',
            clientes: '/clientes'
        }
    }
};
