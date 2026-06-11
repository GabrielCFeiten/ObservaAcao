/* home.js — área do cidadão: criar solicitação e listar recentes. */
Auth.requireLogin();
renderHeader({
  links: [{ href: 'home.html', label: 'Início' }],
  active: 'Início'
});

let categorias = [];

async function carregarCategorias() {
  const sel = document.getElementById('categoria');
  try {
    categorias = await api('/categorias', { auth: false });
    sel.innerHTML = '<option value="">Selecione…</option>' +
      categorias.map(c => `<option value="${c.codigo}">${LABELS.categoria(c.codigo)}</option>`).join('');
  } catch {
    sel.innerHTML = '<option value="">Falha ao carregar categorias</option>';
  }
}

async function carregarSolicitacoes() {
  const ul = document.getElementById('listaSolicitacoes');
  try {
    const lista = await api('/solicitacoes');
    if (!lista.length) {
      ul.innerHTML = '<li class="card empty">Nenhuma solicitação ainda. Crie a primeira ao lado.</li>';
      return;
    }
    ul.innerHTML = lista
      .sort((a, b) => b.protocolo - a.protocolo)
      .map(s => `
        <li class="solic-item">
          <div class="row1">
            <span class="protocolo">#${s.protocolo}</span>
            <span class="badge badge-${s.prioridade}">${LABELS.prioridade[s.prioridade]}</span>
            <span class="badge badge-status">${LABELS.status[s.status]}</span>
          </div>
          <span class="categoria">${LABELS.categoria(s.categoria)}</span>
          <span class="meta">${s.bairro} — ${s.endereco}${s.prazo ? ` · prazo ${s.prazo}` : ''}</span>
        </li>`).join('');
  } catch (err) {
    ul.innerHTML = `<li class="card empty">${err.message}</li>`;
  }
}

document.getElementById('categoria').addEventListener('change', e => {
  const hint = document.getElementById('prioridadeHint');
  const cat = categorias.find(c => c.codigo === e.target.value);
  hint.textContent = cat
    ? (cat.permiteAnonimo ? 'Esta categoria também aceita denúncia anônima.' : '')
    : '';
});

document.getElementById('novaForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('novaMsg');
  msg.className = 'form-msg';
  msg.textContent = 'Enviando…';

  try {
    const salva = await api('/solicitacoes', {
      method: 'POST',
      body: {
        categoria: document.getElementById('categoria').value,
        descricao: document.getElementById('descricao').value.trim(),
        bairro: document.getElementById('bairro').value.trim(),
        endereco: document.getElementById('endereco').value.trim()
      }
    });
    msg.className = 'form-msg ok';
    msg.textContent = `Solicitação registrada! Protocolo #${salva.protocolo}.`;
    e.target.reset();
    carregarSolicitacoes();
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = err.message;
  }
});

carregarCategorias();
carregarSolicitacoes();
