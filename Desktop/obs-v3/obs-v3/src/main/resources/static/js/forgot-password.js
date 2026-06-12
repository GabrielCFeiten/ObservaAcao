document.getElementById('forgotForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('forgotMsg');
  const email = document.getElementById('email').value.trim();
  if (!email) {
    msg.className = 'form-msg error';
    msg.textContent = 'Informe um e-mail válido.';
    return;
  }
  msg.className = 'form-msg';
  msg.textContent = 'Enviando…';
  try {
    const res = await api('/auth/recuperar-senha', {
      method: 'POST',
      auth: false,
      body: { login: email }
    });
    msg.className = 'form-msg ok';
    msg.textContent = res.message || 'Se o e-mail estiver cadastrado, enviaremos um link.';
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = err.message;
  }
});
