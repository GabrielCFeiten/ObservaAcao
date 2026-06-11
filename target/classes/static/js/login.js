/* login.js — autenticação via POST /auth/login */
document.getElementById('loginForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('loginMsg');
  msg.className = 'form-msg';
  msg.textContent = 'Entrando…';

  try {
    const data = await api('/auth/login', {
      method: 'POST',
      auth: false,
      body: {
        login: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value
      }
    });
    Auth.save(data);
    window.location.href = data.tipo === 'GESTOR' ? 'admin-dashboard.html' : 'home.html';
  } catch {
    msg.className = 'form-msg error';
    msg.textContent = 'E-mail ou senha inválidos.';
  }
});

/* ============ DEVELOPMENT ONLY ============
   Atalho: autentica direto via /auth/dev-login (profile dev no backend).
   Remova este bloco — e o .dev-box do HTML — antes de publicar. */
document.getElementById('devBox')?.addEventListener('click', async e => {
  const perfil = e.target.dataset.dev;
  if (!perfil) return;
  const msg = document.getElementById('loginMsg');
  msg.className = 'form-msg';
  msg.textContent = 'Entrando (dev)…';
  try {
    const data = await api(`/auth/dev-login?perfil=${perfil}`, { method: 'POST', auth: false });
    Auth.save(data);
    window.location.href = data.tipo === 'GESTOR' ? 'admin-dashboard.html' : 'home.html';
  } catch {
    msg.className = 'form-msg error';
    msg.textContent = 'Atalho dev indisponível (backend precisa estar no profile "dev").';
  }
});
