Auth.requireLogin();
renderHeader({
  links: [
    { href: 'home.html', label: 'Início' },
    { href: 'solicitar.html', label: 'Solicitar' },
    { href: 'solicitacoes.html', label: 'Solicitações' },
    { href: 'contatos.html', label: 'Contatos' }
  ],
  active: 'Solicitar'
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
    msg.textContent = `✔ Solicitação registrada! Protocolo #${salva.protocolo}.`;
    e.target.reset();
    document.getElementById('prioridadeHint').textContent = '';

    setTimeout(() => {
      msg.innerHTML = `✔ Protocolo #${salva.protocolo} criado. <a href="solicitacoes.html">Ver minhas solicitações →</a>`;
    }, 3000);
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = err.message;
  }
});

carregarCategorias();
