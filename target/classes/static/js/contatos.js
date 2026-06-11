/* contatos.js — exibe os canais de atendimento públicos. */
Auth.requireLogin();
renderHeader({
  links: [
    { href: 'home.html', label: 'Início' },
    { href: 'solicitar.html', label: 'Solicitar' },
    { href: 'solicitacoes.html', label: 'Solicitações' },
    { href: 'contatos.html', label: 'Contatos' }
  ],
  active: 'Contatos'
});

async function carregarContatos() {
  const container = document.getElementById('contatosConteudo');
  try {
    const c = await api('/contatos', { auth: false });
    const campos = [];

    if (c.telefone) campos.push({
      icon: '📞',
      label: 'Telefone',
      value: `<a href="tel:${c.telefone}">${c.telefone}</a>`
    });

    if (c.email) campos.push({
      icon: '✉️',
      label: 'E-mail',
      value: `<a href="mailto:${c.email}">${c.email}</a>`
    });

    if (c.endereco) campos.push({
      icon: '📍',
      label: 'Endereço',
      value: c.endereco
    });

    if (!campos.length) {
      container.innerHTML = '<div class="contatos-vazio">Nenhum canal de contato cadastrado ainda.</div>';
      return;
    }

    container.innerHTML = campos.map(item => `
      <div class="contato-card">
        <div class="contato-icon">${item.icon}</div>
        <div class="contato-body">
          <div class="contato-label">${item.label}</div>
          <div class="contato-value">${item.value}</div>
        </div>
      </div>
    `).join('');

  } catch (err) {
    container.innerHTML = `<div class="card empty">${err.message}</div>`;
  }
}

carregarContatos();
