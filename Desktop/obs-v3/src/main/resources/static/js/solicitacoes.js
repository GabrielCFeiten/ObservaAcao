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

function fmtData(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function fmtDataHora(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

async function carregarSolicitacoes() {
  const ul = document.getElementById('listaSolicitacoes');
  try {
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
      <li class="solic-item" data-protocolo="${s.protocolo}" role="button" tabindex="0">
        <div class="row1">
          <span class="protocolo">#${s.protocolo}</span>
          <span class="badge badge-${s.prioridade}">${LABELS.prioridade[s.prioridade]}</span>
          <span class="badge badge-status">${LABELS.status[s.status]}</span>
        </div>
        <span class="categoria">${LABELS.categoria(s.categoria)}</span>
        <span class="descricao">${s.descricao}</span>
        <span class="meta">${s.bairro} — ${s.endereco}${s.prazo ? ` · prazo ${fmtData(s.prazo)}` : ''}</span>
      </li>`).join('');
}

document.getElementById('statusFilters').addEventListener('click', e => {
  const btn = e.target.closest('.filter-btn');
  if (!btn) return;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  filtroAtivo = btn.dataset.status;
  renderLista();
});

document.getElementById('listaSolicitacoes').addEventListener('click', e => {
  const item = e.target.closest('.solic-item');
  if (!item) return;
  abrirDetalhe(Number(item.dataset.protocolo));
});

async function abrirDetalhe(protocolo) {
  const s = todasSolicitacoes.find(x => x.protocolo === protocolo);
  if (!s) return;

  document.getElementById('detalheTitle').textContent = `Solicitação #${s.protocolo}`;
  document.getElementById('detalheInfo').innerHTML = `
    <div class="detalhe-grid">
      <div class="detalhe-campo"><span class="detalhe-label">Categoria</span><span>${LABELS.categoria(s.categoria)}</span></div>
      <div class="detalhe-campo"><span class="detalhe-label">Prioridade</span><span><span class="badge badge-${s.prioridade}">${LABELS.prioridade[s.prioridade]}</span></span></div>
      <div class="detalhe-campo"><span class="detalhe-label">Status</span><span><span class="badge badge-status">${LABELS.status[s.status]}</span></span></div>
      <div class="detalhe-campo"><span class="detalhe-label">Prazo</span><span>${s.prazo ? fmtData(s.prazo) : '—'}</span></div>
      <div class="detalhe-campo detalhe-full"><span class="detalhe-label">Local</span><span>${s.bairro} — ${s.endereco}</span></div>
      <div class="detalhe-campo detalhe-full"><span class="detalhe-label">Descrição</span><span>${s.descricao}</span></div>
    </div>`;

  document.getElementById('detalheHistorico').innerHTML = '<p class="hist-carregando">Carregando histórico…</p>';
  document.getElementById('detalheBackdrop').classList.add('open');

  try {
    const historico = await api(`/solicitacoes/${protocolo}/historico`);
    renderHistorico(historico);
  } catch (err) {
    document.getElementById('detalheHistorico').innerHTML = `<p class="hist-erro">${err.message}</p>`;
  }
}

function renderHistorico(historico) {
  const container = document.getElementById('detalheHistorico');
  if (!historico.length) {
    container.innerHTML = '<p class="hist-vazio">Sem movimentações registradas.</p>';
    return;
  }
  container.innerHTML = historico.map(h => `
    <div class="hist-item">
      <div class="hist-header">
        <span class="badge badge-status">${LABELS.status[h.status]}</span>
        <span class="hist-data">${fmtDataHora(h.dataMovimentacao)}</span>
      </div>
      <p class="hist-comentario">${h.justificativa || '—'}</p>
      <p class="hist-responsavel">Responsável: <strong>${h.responsavel ? h.responsavel.nome : 'Sistema'}</strong></p>
    </div>`).join('');
}

document.getElementById('detalheClose').addEventListener('click', () =>
  document.getElementById('detalheBackdrop').classList.remove('open'));

document.getElementById('detalheBackdrop').addEventListener('click', e => {
  if (e.target === document.getElementById('detalheBackdrop'))
    document.getElementById('detalheBackdrop').classList.remove('open');
});

carregarSolicitacoes();
