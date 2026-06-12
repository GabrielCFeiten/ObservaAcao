function renderHeader({ links = [], active = '' } = {}) {
  const initial = (Auth.nome.trim()[0] || 'U').toUpperCase();

  const header = document.createElement('header');
  header.className = 'app-header';
  header.innerHTML = `
    <div class="container header-inner">
      <span class="brand">Observa<span class="dot">Ação</span></span>
      <nav class="main-nav">
        ${links.map(l =>
          `<a href="${l.href}" class="${l.label === active ? 'active' : ''}">${l.label}</a>`
        ).join('')}
      </nav>
      <span class="header-spacer"></span>
      <div class="profile-wrap">
        <button class="avatar-btn" id="avatarBtn" aria-haspopup="true" aria-expanded="false"
                title="${Auth.nome}">${initial}</button>
        <div class="profile-menu" id="profileMenu" role="menu">
          <div class="menu-name">${Auth.nome}</div>
          <button id="menuSettings" role="menuitem">Perfil</button>
          <button id="menuLogout" role="menuitem">Sair</button>
        </div>
      </div>
    </div>`;
  document.body.prepend(header);
  document.body.insertAdjacentHTML('beforeend', settingsModalHTML());

  const btn = header.querySelector('#avatarBtn');
  const menu = header.querySelector('#profileMenu');

  btn.addEventListener('click', e => {
    e.stopPropagation();
    const open = menu.classList.toggle('open');
    btn.setAttribute('aria-expanded', String(open));
  });
  document.addEventListener('click', () => menu.classList.remove('open'));

  header.querySelector('#menuLogout').addEventListener('click', () => Auth.logout());
  header.querySelector('#menuSettings').addEventListener('click', () => {
    menu.classList.remove('open');
    document.getElementById('settingsBackdrop').classList.add('open');
  });

  wireSettingsModal();
}

function settingsModalHTML() {
  return `
  <div class="modal-backdrop" id="settingsBackdrop">
    <div class="modal" role="dialog" aria-labelledby="settingsTitle">
      <div class="modal-header">
        <h3 id="settingsTitle">Configurações da conta</h3>
        <button class="modal-close" data-close="settingsBackdrop" aria-label="Fechar">&times;</button>
      </div>
      <form id="settingsForm" novalidate>
        <div class="field">
          <label for="setNome">Nome completo</label>
          <input id="setNome" type="text" value="${Auth.nome}">
        </div>
        <div class="field">
          <label for="setEmail">E-mail (login)</label>
          <input id="setEmail" type="email" placeholder="seu@email.com">
        </div>
        <div class="field">
          <label for="setTelefone">Telefone</label>
          <input id="setTelefone" type="tel" placeholder="(44) 99999-9999">
        </div>
        <div class="field">
          <label for="setSenha">Nova senha</label>
          <input id="setSenha" type="password" placeholder="Deixe em branco para manter">
        </div>
        <button class="btn btn-primary btn-block" type="submit">Salvar alterações</button>
        <p class="form-msg" id="settingsMsg"></p>
      </form>
    </div>
  </div>`;
}

function wireSettingsModal() {
  const backdrop = document.getElementById('settingsBackdrop');
  backdrop.addEventListener('click', e => {
    if (e.target === backdrop || e.target.dataset.close) backdrop.classList.remove('open');
  });

  document.getElementById('menuSettings')?.addEventListener('click', carregarPerfil);
  document.getElementById('adminCard')?.addEventListener('click', carregarPerfil);

  document.getElementById('settingsForm').addEventListener('submit', async e => {
    e.preventDefault();
    const msg = document.getElementById('settingsMsg');
    msg.className = 'form-msg';
    msg.textContent = 'Salvando…';
    try {
      const atualizado = await api('/usuarios/me', {
        method: 'PATCH',
        body: {
          nome: document.getElementById('setNome').value.trim(),
          login: document.getElementById('setEmail').value.trim(),
          telefone: document.getElementById('setTelefone').value.trim(),
          password: document.getElementById('setSenha').value || null
        }
      });
      Auth.setNome(atualizado.nome);
      msg.className = 'form-msg ok';
      msg.textContent = 'Dados atualizados.';
      setTimeout(() => window.location.reload(), 800);
    } catch (err) {
      msg.className = 'form-msg error';
      msg.textContent = err.message;
    }
  });
}

async function carregarPerfil() {
  try {
    const me = await api('/usuarios/me');
    document.getElementById('setNome').value = me.nome || '';
    document.getElementById('setEmail').value = me.login || '';
    document.getElementById('setTelefone').value = me.telefone || '';
    document.getElementById('setSenha').value = '';
  } catch {  }
}
