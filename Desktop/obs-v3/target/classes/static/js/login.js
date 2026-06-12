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
