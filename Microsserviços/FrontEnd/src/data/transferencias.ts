import { DataModel, DataSource } from '@toolpad/core/Crud';
import { z } from 'zod';

export interface Transferencia extends DataModel {
  id: number;
  contaOrigemId: number;
  contaDestinoId: number;
  valor: number;
  moeda: string;
  status: string;
}

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8085';
const API_BASE = `${GATEWAY_URL}/transferencias`;

const handleResponse = async (res: Response) => {
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || res.statusText);
  }
  return res.json();
};

export const transferenciasDataSource: DataSource<Transferencia> = {
  fields: [
    { field: 'id', headerName: 'ID', width: 80 },
    { field: 'contaOrigemId', headerName: 'Conta Origem', width: 140 },
    { field: 'contaDestinoId', headerName: 'Conta Destino', width: 140 },
    { field: 'valor', headerName: 'Valor', type: 'number', width: 130 },
    { field: 'moeda', headerName: 'Moeda', width: 100 },
    { field: 'status', headerName: 'Status', width: 220 },
  ],
  getMany: async ({ paginationModel }) => {
    const res = await fetch(API_BASE);
    const lista = (await handleResponse(res)) as Transferencia[];

    const page = paginationModel?.page ?? 0;
    const pageSize = paginationModel?.pageSize ?? lista.length;
    const start = page * pageSize;
    const end = start + pageSize;
    const items = lista.slice(start, end);

    return { items, itemCount: lista.length };
  },
  getOne: async (id) => {
    const res = await fetch(`${API_BASE}/${id}`);
    const data = await handleResponse(res);
    if (!data) throw new Error('Transferência não encontrada');
    return data as Transferencia;
  },
  createOne: async (data) => {
    const payload = {
      contaOrigemId: Number(data.contaOrigemId),
      contaDestinoId: Number(data.contaDestinoId),
      valor: Number(data.valor),
      moeda: data.moeda || 'BRL',
    };
    const res = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return (await handleResponse(res)) as Transferencia;
  },
  updateOne: async () => {
    throw new Error('Transferências não podem ser alteradas diretamente (imutabilidade de saga).');
  },
  deleteOne: async () => {
    throw new Error('Transferências não podem ser excluídas diretamente.');
  },
  validate: z.object({
    contaOrigemId: z.coerce.number({ required_error: 'Conta de origem é obrigatória' }),
    contaDestinoId: z.coerce.number({ required_error: 'Conta de destino é obrigatória' }),
    valor: z.coerce.number({ required_error: 'Valor é obrigatório' }).positive('Valor deve ser positivo'),
    moeda: z.string().optional().default('BRL'),
  })['~standard'].validate,
};
