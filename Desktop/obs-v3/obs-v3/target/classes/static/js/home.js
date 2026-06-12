Auth.requireLogin();
renderHeader({
  links: [
    { href: 'home.html', label: 'Início' },
    { href: 'solicitar.html', label: 'Solicitar' },
    { href: 'solicitacoes.html', label: 'Solicitações' },
    { href: 'contatos.html', label: 'Contatos' }
  ],
  active: 'Início'
});

const h = new Date().getHours();
const periodo = h < 12 ? 'Bom dia' : h < 18 ? 'Boa tarde' : 'Boa noite';
document.getElementById('greetingText').textContent = `${periodo}, ${Auth.nome.split(' ')[0]}!`;
