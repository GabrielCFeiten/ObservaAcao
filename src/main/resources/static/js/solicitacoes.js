/* solicitacoes.js — lista as solicitações do usuário logado, filtrando por status. */
Auth.requireLogin();
renderHeader({
  links: [
    { href: 'home.html', label: 'Início' },
    { href: 'solicitar.html', label: 'Solicitar' },
    { href: 'solicitacoes.html', label: 'Solicitações' },
    { href: 'contatos.html', label: 'Contatos' }
  ],
  active: 'Solicitações'
});

let todasSolicitacoes = [];
let filtroAtivo = 'TODOS';

async function carregarSolicitacoes() {
  const ul = document.getElementById('listaSolicitacoes');
  try {
    // Usa o endpoint que filtra pelo usuário do token JWT
    todasSolicitacoes = await api('/solicitacoes/minhas');
    renderLista();
  } catch (err) {
    ul.innerHTML = `<li class="card empty">${err.message}</li>`;
  }
}

function renderLista() {
  const ul = document.getElementById('listaSolicitacoes');
  const filtradas = filtroAtivo === 'TODOS'
    ? todasSolicitacoes
    : todasSolicitacoes.filter(s => s.status === filtroAtivo);

  if (!filtradas.length) {
    const msg = filtroAtivo === 'TODOS'
      ? 'Nenhuma solicitação ainda. <a href="solicitar.html">Crie a primeira →</a>'
      : `Nenhuma solicitação com status <strong>${LABELS.status[filtroAtivo]}</strong>.`;
    ul.innerHTML = `<li class="card empty">${msg}</li>`;
    return;
  }

  ul.innerHTML = filtradas
    .sort((a, b) => b.protocolo - a.protocolo)
    .map(s => `
      <li class="solic-item">
        <div class="row1">
          <span class="protocolo">#${s.protocolo}</span>
          <span class="badge badge-${s.prioridade}">${LABELS.prioridade[s.prioridade]}</span>
          <span class="badge badge-status">${LABELS.status[s.status]}</span>
        </div>
        <span class="categoria">${LABELS.categoria(s.categoria)}</span>
        <span class="descricao">${s.descricao}</span>
        <span class="meta">${s.bairro} — ${s.endereco}${s.prazo ? ` · prazo ${s.prazo}` : ''}</span>
      </li>`).join('');
}

// Filtros de status
document.getElementById('statusFilters').addEventListener('click', e => {
  const btn = e.target.closest('.filter-btn');
  if (!btn) return;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  filtroAtivo = btn.dataset.status;
  renderLista();
});

carregarSolicitacoes();
