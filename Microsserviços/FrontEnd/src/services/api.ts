import {
  Conta,
  Transferencia,
  User,
  LoginRequest,
  RegistrarClienteRequest,
  CriarContaRequest,
  IniciarTransferenciaCommand,
} from '../types';

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8085';

export function decodeJwt(token: string): { sub: string; clienteId: number } | null {
  try {
    const parts = token.split('.');
    if (parts.length < 2) return null;
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const parsed = JSON.parse(jsonPayload);
    return {
      sub: parsed.sub || '',
      clienteId: Number(parsed.clienteId || 0),
    };
  } catch (err) {
    console.error('Falha ao decodificar token JWT:', err);
    return null;
  }
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let errorMsg = res.statusText;
    try {
      const contentType = res.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const errorJson = await res.json();
        errorMsg = errorJson.message || errorJson.error || JSON.stringify(errorJson);
      } else {
        const text = await res.text();
        if (text) errorMsg = text;
      }
    } catch {
      // Ignora erro de parsing e usa statusText
    }
    throw new Error(errorMsg || `Erro na requisição: ${res.status}`);
  }

  const contentType = res.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return (await res.json()) as T;
  }
  const text = await res.text();
  return text as unknown as T;
}

function getAuthHeaders(token?: string | null): Record<string, string> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  const effectiveToken = token || localStorage.getItem('fintech_token');
  if (effectiveToken) {
    headers['Authorization'] = `Bearer ${effectiveToken}`;
  }
  return headers;
}

export const api = {
  auth: {
    async login(payload: LoginRequest): Promise<{ token: string; tipo: string; user: User }> {
      const res = await fetch(`${GATEWAY_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const data = await handleResponse<{ token: string; tipo?: string }>(res);
      const decoded = decodeJwt(data.token);
      const user: User = {
        id: decoded?.clienteId || 0,
        nome: decoded?.sub || payload.nome,
      };
      return {
        token: data.token,
        tipo: data.tipo || 'Bearer',
        user,
      };
    },

    async registrar(payload: RegistrarClienteRequest): Promise<{ message: string; clienteId?: number }> {
      const res = await fetch(`${GATEWAY_URL}/clientes/registrar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      const text = await handleResponse<string>(res);
      // Exemplo de resposta: "Cliente registrado com sucesso! ID: 3"
      const match = typeof text === 'string' ? text.match(/ID:\s*(\d+)/i) : null;
      const clienteId = match ? Number(match[1]) : undefined;
      return { message: typeof text === 'string' ? text : 'Cliente registrado!', clienteId };
    },
  },

  contas: {
    async listar(token?: string | null): Promise<Conta[]> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/listar`, {
        headers: getAuthHeaders(token),
      });
      return handleResponse<Conta[]>(res);
    },

    async obter(id: number, token?: string | null): Promise<Conta> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/listar/${id}`, {
        headers: getAuthHeaders(token),
      });
      return handleResponse<Conta>(res);
    },

    async criar(payload: CriarContaRequest, token?: string | null): Promise<string> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/adicionar`, {
        method: 'POST',
        headers: getAuthHeaders(token),
        body: JSON.stringify(payload),
      });
      return handleResponse<string>(res);
    },

    async creditar(id: number, valor: number, token?: string | null): Promise<string> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/creditar/${id}/${valor}`, {
        method: 'PUT',
        headers: getAuthHeaders(token),
      });
      return handleResponse<string>(res);
    },

    async debitar(id: number, valor: number, token?: string | null): Promise<string> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/debitar/${id}/${valor}`, {
        method: 'PUT',
        headers: getAuthHeaders(token),
      });
      return handleResponse<string>(res);
    },

    async deletar(id: number, token?: string | null): Promise<string> {
      const res = await fetch(`${GATEWAY_URL}/contas-banco/delete/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(token),
      });
      return handleResponse<string>(res);
    },
  },

  transferencias: {
    async iniciar(payload: IniciarTransferenciaCommand, token?: string | null): Promise<Transferencia> {
      const res = await fetch(`${GATEWAY_URL}/transferencias`, {
        method: 'POST',
        headers: getAuthHeaders(token),
        body: JSON.stringify(payload),
      });
      return handleResponse<Transferencia>(res);
    },

    async listar(token?: string | null): Promise<Transferencia[]> {
      const res = await fetch(`${GATEWAY_URL}/transferencias`, {
        headers: getAuthHeaders(token),
      });
      return handleResponse<Transferencia[]>(res);
    },

    async obter(id: number, token?: string | null): Promise<Transferencia> {
      const res = await fetch(`${GATEWAY_URL}/transferencias/${id}`, {
        headers: getAuthHeaders(token),
      });
      return handleResponse<Transferencia>(res);
    },
  },
};
