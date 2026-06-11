/* register.js — cadastro via POST /usuarios/cadastro (público, tipo CIDADAO) */
document.getElementById('registerForm').addEventListener('submit', async e => {
  e.preventDefault();
  const msg = document.getElementById('registerMsg');
  const val = id => document.getElementById(id).value.trim();

  if (val('password') !== val('confirm')) {
    msg.className = 'form-msg error';
    msg.textContent = 'As senhas não coincidem.';
    return;
  }

  msg.className = 'form-msg';
  msg.textContent = 'Cadastrando…';

  try {
    await api('/usuarios/cadastro', {
      method: 'POST',
      auth: false,
      body: {
        nome: val('nome'),
        cpf: val('cpf'),
        telefone: val('telefone'),
        login: val('email'),
        password: document.getElementById('password').value
      }
    });
    msg.className = 'form-msg ok';
    msg.textContent = 'Conta criada! Redirecionando para o login…';
    setTimeout(() => (window.location.href = 'login.html'), 1200);
  } catch (err) {
    msg.className = 'form-msg error';
    msg.textContent = 'Não foi possível cadastrar. Verifique os dados (e-mail já usado?).';
  }
});
