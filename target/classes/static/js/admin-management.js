/* admin-management.js — diretório de administradores.
   Backend:
   - GET   /usuarios/gestores   (lista todos os GESTOR)
   - POST  /usuarios/cadastro   (cria CIDADAO)
   - PATCH /usuarios/{id}/tipo  (promove GESTOR / rebaixa CIDADAO) */

Auth.requireGestor();
renderHeader({
  links: [
    { href: 'admin-dashboard.html', label: 'Solicitações' },
    { href: 'admin-contacts.html', label: 'Contatos' },
    { href: 'admin-management.html', label: 'Admins' }
  ],
  active: 'Admins'
});

async function renderLista() {
  const ul = document.getElementById('adminList');
  try {
    const admins = await api('/usuarios/gestores');
    if (!admins.length) {
      ul.innerHTML = '<li class="card empty">Nenhum administrador cadastrado.</li>';
      return;
    }
    const meuId = Auth.id;
    ul.innerHTML = admins.map(a => `
      <li class="admin-item" data-id="${a.id}">
        <span class="ad-avatar">${(a.nome[0] || 'A').toUpperCase()}</span>
        <span class="ad-info">
          <span class="ad-nome">${a.nome}</span><br>
          <span class="ad-login">${a.login} · ID ${a.id}</span>
        </span>
        ${String(a.id) === String(meuId)
          ? '<span class="badge badge-status">você</span>'
          : `<button class="btn btn-sm btn-danger" data-remove="${a.id}">Remover</button>`}
      </li>`).join('');
  } catch (err) {
    ul.innerHTML = `<li class="card empty">${err.message}</li>`;
  }
}

/* Criar: cadastro público + promoção imediata para GESTOR */
document.getElementById('newAdminForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('newAdminMsg');
  const val = id => document.getElementById(id).value.trim();

  msg.className = 'form-msg';
  msg.textContent = 'Criando…';
  try {
    const criado = await api('/usuarios/cadastro', {
      method: 'POST',
      auth: false,
      body: {
        nome: val('naNome'),
        cpf: val('naCpf'),
        telefone: val('naTelefone'),
        login: val('naEmail'),
        password: document.getElementById('naSenha').value
      }
    });
    await api(`/usuarios/${criado.id}/tipo`, {
      method: 'PATCH',
      body: { novoTipo: 'GESTOR' }
    });
    msg.className = 'form-msg ok';
    msg.textContent = `Administrador ${criado.nome} criado.`;
    e.target.reset();
    renderLista();
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = 'Falha ao criar administrador: ' + err.message;
  }
});

/* Remover: rebaixa para CIDADAO */
document.getElementById('adminList').addEventListener('click', async e => {
  const id = e.target.dataset.remove;
  if (!id) return;
  if (!confirm('Revogar privilégios de gestor deste administrador?')) return;
  try {
    await api(`/usuarios/${id}/tipo`, {
      method: 'PATCH',
      body: { novoTipo: 'CIDADAO' }
    });
    renderLista();
  } catch (err) {
    alert('Falha ao remover: ' + err.message);
  }
});

renderLista();
