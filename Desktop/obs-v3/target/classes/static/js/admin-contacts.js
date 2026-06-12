Auth.requireGestor();
renderHeader({
  links: [
    { href: 'admin-dashboard.html', label: 'Solicitações' },
    { href: 'admin-contacts.html', label: 'Contatos' },
    { href: 'admin-management.html', label: 'Admins' }
  ],
  active: 'Contatos'
});

async function carregar() {
  try {
    const c = await api('/contatos', { auth: false });
    document.getElementById('ctEmail').value = c.email || '';
    document.getElementById('ctTelefone').value = c.telefone || '';
    document.getElementById('ctEndereco').value = c.endereco || '';
    renderPreview(c);
  } catch (err) {
    const msg = document.getElementById('contactsMsg');
    msg.className = 'form-msg error';
    msg.textContent = 'Falha ao carregar contatos: ' + err.message;
  }
}

function renderPreview(c) {
  document.getElementById('contactsPreview').innerHTML = `
    <dt>E-mail</dt><dd>${c.email || '—'}</dd>
    <dt>Telefone</dt><dd>${c.telefone || '—'}</dd>
    <dt>Endereço</dt><dd>${c.endereco || '—'}</dd>`;
}

document.getElementById('contactsForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('contactsMsg');
  msg.className = 'form-msg';
  msg.textContent = 'Salvando…';
  try {
    const novo = await api('/contatos', {
      method: 'PUT',
      body: {
        email: document.getElementById('ctEmail').value.trim(),
        telefone: document.getElementById('ctTelefone').value.trim(),
        endereco: document.getElementById('ctEndereco').value.trim()
      }
    });
    renderPreview(novo);
    msg.className = 'form-msg ok';
    msg.textContent = 'Contatos atualizados.';
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = err.message;
  }
});

carregar();
