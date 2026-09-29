import { DataModel, DataSource } from '@toolpad/core/Crud';
import { z } from 'zod';

export interface Conta extends DataModel {
  id: number;
  nome: string;
  saldoValor: number;
  saldoMoeda: string;
}

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8085';
const API_BASE = `${GATEWAY_URL}/contas-banco`;

const handleResponse = async (res: Response) => {
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || res.statusText);
  }
  return res.json();
};

export const contasDataSource: DataSource<Conta> = {
  fields: [
    { field: 'id', headerName: 'ID', width: 80 },
    { field: 'nome', headerName: 'Nome', width: 200 },
    { field: 'saldoValor', headerName: 'Saldo', type: 'number', width: 150 },
    { field: 'saldoMoeda', headerName: 'Moeda', width: 100 },
  ],
  getMany: async ({ paginationModel, filterModel, sortModel }) => {
    const res = await fetch(`${API_BASE}/listar`);
    const contas = (await handleResponse(res)) as Conta[];

    let filtered = [...contas];

    if (filterModel?.items?.length) {
      filterModel.items.forEach(({ field, value, operator }) => {
        if (!field || value == null) return;

        filtered = filtered.filter((item) => {
          const v = item[field as keyof Conta] as any;
          switch (operator) {
            case 'contains':
              return String(v).toLowerCase().includes(String(value).toLowerCase());
            case 'equals':
              return v === value;
            case 'startsWith':
              return String(v).toLowerCase().startsWith(String(value).toLowerCase());
            case 'endsWith':
              return String(v).toLowerCase().endsWith(String(value).toLowerCase());
            case '>':
              return (v as number) > value;
            case '<':
              return (v as number) < value;
            default:
              return true;
          }
        });
      });
    }

    if (sortModel?.length) {
      filtered.sort((a, b) => {
        for (const { field, sort } of sortModel) {
          const av = a[field as keyof Conta] as any;
          const bv = b[field as keyof Conta] as any;
          if (av < bv) return sort === 'asc' ? -1 : 1;
          if (av > bv) return sort === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }

    const page = paginationModel?.page ?? 0;
    const pageSize = paginationModel?.pageSize ?? filtered.length;
    const start = page * pageSize;
    const end = start + pageSize;
    const items = filtered.slice(start, end);

    return { items, itemCount: filtered.length };
  },
  getOne: async (id) => {
    const res = await fetch(`${API_BASE}/listar/${id}`);
    const data = await handleResponse(res);
    if (!data) throw new Error('Conta não encontrada');
    return data as Conta;
  },
  createOne: async (data) => {
    const payload = {
      nome: data.nome,
      saldoInicial: Number(data.saldoValor),
      moeda: data.saldoMoeda || 'BRL',
    };
    await fetch(`${API_BASE}/adicionar`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then(async (res) => {
      if (!res.ok) {
        const t = await res.text();
        throw new Error(t || res.statusText);
      }
    });
    const res = await fetch(`${API_BASE}/listar`);
    const contas = (await handleResponse(res)) as Conta[];
    const newId = contas.reduce((max, c) => (c.id > max ? c.id : max), 0);
    return contas.find((c) => c.id === newId) as Conta;
  },
  updateOne: async (id, data) => {
    const saldo = Number((data as Partial<Conta>).saldoValor);
    if (isNaN(saldo)) {
      throw new Error('Valor de saldo inválido');
    }
    await fetch(`${API_BASE}/creditar/${id}/${saldo}`, { method: 'PUT' }).then(async (res) => {
      if (!res.ok) {
        const t = await res.text();
        throw new Error(t || res.statusText);
      }
    });
    const res = await fetch(`${API_BASE}/listar/${id}`);
    const updated = await handleResponse(res);
    if (!updated) throw new Error('Conta não encontrada');
    return updated as Conta;
  },
  deleteOne: async (id) => {
    await fetch(`${API_BASE}/delete/${id}`, { method: 'DELETE' }).then(async (res) => {
      if (!res.ok) {
        const t = await res.text();
        throw new Error(t || res.statusText);
      }
    });
  },
  validate: z.object({
    nome: z.string({ required_error: 'Nome é obrigatório' }).min(1, 'Nome é obrigatório'),
    saldoValor: z.coerce.number({ required_error: 'Saldo é obrigatório' }),
    saldoMoeda: z.string().optional().default('BRL'),
  })['~standard'].validate,
};
