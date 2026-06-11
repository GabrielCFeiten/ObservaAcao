/* admin-dashboard.js — painel do gestor.
   Consome: GET /solicitacoes, PATCH /solicitacoes/{protocolo}/status */

Auth.requireGestor();
renderHeader({
  links: [
    { href: 'admin-dashboard.html', label: 'Solicitações' },
    { href: 'admin-contacts.html', label: 'Contatos' },
    { href: 'admin-management.html', label: 'Admins' }
  ],
  active: 'Solicitações'
});

let todas = [];
let filtroAtual = 'TODOS';
let selecionada = null;

/* ---------- Carga e renderização ---------- */
async function carregar() {
  try {
    todas = await api('/solicitacoes');
  } catch (err) {
    document.getElementById('colALTA').innerHTML =
      `<li class="col-empty">${err.message}</li>`;
    return;
  }
  renderColunas();
  renderResumo();
}

function aplicarFiltro(lista) {
  if (filtroAtual === 'URGENTES') return lista.filter(s => s.prioridade === 'ALTA');
  if (filtroAtual === 'HOJE') {
    const hoje = new Date().toISOString().slice(0, 10);
    return lista.filter(s => s.prazo === hoje);
  }
  return lista;
}

function renderColunas() {
  const visiveis = aplicarFiltro(todas);
  PRIORIDADE_LIST.forEach(p => {
    const itens = visiveis
      .filter(s => s.prioridade === p)
      .sort((a, b) => b.protocolo - a.protocolo);
    document.getElementById('count' + p).textContent = itens.length;
    const ul = document.getElementById('col' + p);
    ul.innerHTML = itens.length
      ? itens.map(itemHTML).join('')
      : '<li class="col-empty">Nenhuma solicitação</li>';
  });
}

function itemHTML(s) {
  return `
    <li class="req-item" data-protocolo="${s.protocolo}">
      <div class="req-top">
        <span class="protocolo">#${s.protocolo}</span>
        <span class="badge badge-status">${LABELS.status[s.status]}</span>
      </div>
      <div class="req-cat">${LABELS.categoria(s.categoria)}</div>
      <div class="req-meta">${s.bairro}${s.prazo ? ` · prazo ${s.prazo}` : ''}</div>
    </li>`;
}

function renderResumo() {
  const total = todas.length;
  document.getElementById('statTotal').textContent = total;
  document.getElementById('statUrgentes').textContent =
    todas.filter(s => s.prioridade === 'ALTA').length;
  document.getElementById('statResolvidas').textContent =
    todas.filter(s => s.status === 'RESOLVIDO' || s.status === 'ENCERRADO').length;

  const bar = document.getElementById('miniBar');
  bar.innerHTML = PRIORIDADE_LIST.map(p => {
    const pct = total ? (todas.filter(s => s.prioridade === p).length / total) * 100 : 0;
    return `<span class="seg-${p}" style="width:${pct}%"></span>`;
  }).join('');
}

/* ---------- Filtros rápidos ---------- */
document.querySelectorAll('.filter-chip').forEach(chip => {
  chip.addEventListener('click', () => {
    document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
    chip.classList.add('active');
    filtroAtual = chip.dataset.filter;
    renderColunas();
  });
});

/* Clique no cabeçalho da coluna: foca somente naquela prioridade */
let colunaIsolada = null;
document.querySelectorAll('.col-head').forEach(head => {
  head.addEventListener('click', () => {
    const prio = head.closest('.col').dataset.prio;
    colunaIsolada = colunaIsolada === prio ? null : prio; // clique de novo = mostrar todas
    document.querySelectorAll('.col').forEach(col => {
      col.style.display =
        colunaIsolada && col.dataset.prio !== colunaIsolada ? 'none' : '';
    });
  });
});

/* ---------- A. Card do admin → editar conta ---------- */
document.getElementById('adminNome').textContent = Auth.nome;
document.getElementById('adminInitial').textContent =
  (Auth.nome.trim()[0] || 'G').toUpperCase();
document.getElementById('adminCard').addEventListener('click', () =>
  document.getElementById('settingsBackdrop').classList.add('open'));

/* ---------- B. Card de métricas → análise detalhada ---------- */
document.getElementById('statsCard').addEventListener('click', abrirAnalise);

function abrirAnalise() {
  const total = todas.length || 1;
  const sev = { ALTA: 'Urgentes', MEDIA: 'Médias', BAIXA: 'Leves' };

  document.getElementById('severityBars').innerHTML =
    PRIORIDADE_LIST.map(p => barHTML(
      sev[p],
      todas.filter(s => s.prioridade === p).length,
      total,
      `f-${p}`
    )).join('');

  document.getElementById('statusBars').innerHTML =
    STATUS_LIST.map(st => barHTML(
      st,
      todas.filter(s => s.status === st).length,
      total,
      ''
    )).join('');

  document.getElementById('statsBackdrop').classList.add('open');
}

function barHTML(label, count, total, cls) {
  const pct = ((count / total) * 100).toFixed(1);
  return `
    <div class="bar-line">
      <div class="bar-label"><span>${label}</span><span>${pct}% (${count})</span></div>
      <div class="bar-track"><div class="bar-fill ${cls}" style="width:${pct}%"></div></div>
    </div>`;
}

/* ---------- C. Gestão de solicitação ---------- */
document.getElementById('columns').addEventListener('click', e => {
  const item = e.target.closest('.req-item');
  if (!item) return;
  selecionada = todas.find(s => s.protocolo === Number(item.dataset.protocolo));
  if (!selecionada) return;

  document.getElementById('manageTitle').textContent = `Solicitação #${selecionada.protocolo}`;
  document.getElementById('manageInfo').innerHTML = `
    <span><strong>Categoria:</strong> ${LABELS.categoria(selecionada.categoria)}</span>
    <span><strong>Prioridade:</strong> ${LABELS.prioridade[selecionada.prioridade]}</span>
    <span><strong>Status atual:</strong> ${LABELS.status[selecionada.status]}</span>
    <span><strong>Local:</strong> ${selecionada.bairro} — ${selecionada.endereco}</span>
    <span><strong>Descrição:</strong> ${selecionada.descricao || '—'}</span>`;

  document.getElementById('novoStatus').innerHTML =
    STATUS_LIST.map(st =>
      `<option value="${st}" ${st === selecionada.status ? 'selected' : ''}>${LABELS.status[st]}</option>`
    ).join('');

  document.getElementById('comentario').value = '';
  document.getElementById('manageMsg').textContent = '';
  document.getElementById('manageBackdrop').classList.add('open');
});

document.getElementById('manageForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('manageMsg');
  const novoStatus = document.getElementById('novoStatus').value;
  const comentario = document.getElementById('comentario').value.trim();

  if (!comentario) {
    msg.className = 'form-msg error';
    msg.textContent = 'O comentário (justificativa) é obrigatório.';
    return;
  }

  msg.className = 'form-msg';
  msg.textContent = 'Salvando…';
  try {
    await api(
      `/solicitacoes/${selecionada.protocolo}/status` +
      `?novoStatus=${novoStatus}&justificativa=${encodeURIComponent(comentario)}`,
      { method: 'PATCH' }
    );
    msg.className = 'form-msg ok';
    msg.textContent = 'Movimentação registrada.';
    await carregar();
    setTimeout(() =>
      document.getElementById('manageBackdrop').classList.remove('open'), 700);
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = err.message;
  }
});

/* ---------- Fechar modais ---------- */
['statsBackdrop', 'manageBackdrop'].forEach(id => {
  const bd = document.getElementById(id);
  bd.addEventListener('click', e => {
    if (e.target === bd || e.target.classList.contains('modal-close'))
      bd.classList.remove('open');
  });
});

carregar();
