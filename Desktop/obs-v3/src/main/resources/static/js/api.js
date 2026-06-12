const API_BASE = '';

const Auth = {
  save({ id, token, tipo, nome }) {
    if (id != null) localStorage.setItem('oa_id', id);
    localStorage.setItem('oa_token', token);
    localStorage.setItem('oa_tipo', tipo);
    localStorage.setItem('oa_nome', nome);
  },
  get id()    { return localStorage.getItem('oa_id'); },
  get token() { return localStorage.getItem('oa_token'); },
  get tipo()  { return localStorage.getItem('oa_tipo'); },
  get nome()  { return localStorage.getItem('oa_nome') || 'Usuário'; },
  get logged() { return Boolean(this.token); },
  setNome(n)  { localStorage.setItem('oa_nome', n); },
  logout() {
    ['oa_id', 'oa_token', 'oa_tipo', 'oa_nome'].forEach(k => localStorage.removeItem(k));
    window.location.href = 'login.html';
  },
  requireLogin() {
    if (!this.logged) window.location.href = 'login.html';
  },
  requireGestor() {
    this.requireLogin();
    if (this.tipo !== 'GESTOR') window.location.href = 'home.html';
  }
};

async function api(path, { method = 'GET', body, auth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth && Auth.token) headers['Authorization'] = `Bearer ${Auth.token}`;

  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  if (res.status === 401 || res.status === 403) {
    if (auth) throw new Error('Sessão expirada ou sem permissão. Faça login novamente.');
  }

  if (!res.ok) {
    let mensagem = 'Ocorreu um erro. Tente novamente.';
    try {
      const dados = await res.json();
      if (dados && dados.message) mensagem = dados.message;
    } catch {
      const texto = await res.text().catch(() => '');
      if (texto) mensagem = texto;
    }
    throw new Error(mensagem);
  }

  const ct = res.headers.get('content-type') || '';
  return ct.includes('json') ? res.json() : res.text();
}

const LABELS = {
  prioridade: { ALTA: 'Urgente', MEDIA: 'Médio', BAIXA: 'Leve' },
  status: {
    ABERTO: 'Aberto',
    TRIAGEM: 'Triagem',
    EM_EXECUCAO: 'Em execução',
    RESOLVIDO: 'Resolvido',
    ENCERRADO: 'Encerrado'
  },
  categoria(code) {
    return code
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/^./, c => c.toUpperCase());
  }
};

const STATUS_LIST = ['ABERTO', 'TRIAGEM', 'EM_EXECUCAO', 'RESOLVIDO', 'ENCERRADO'];
const PRIORIDADE_LIST = ['ALTA', 'MEDIA', 'BAIXA'];
