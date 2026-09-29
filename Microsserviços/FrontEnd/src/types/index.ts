export interface User {
  id: number;
  nome: string;
}

export interface AuthSession {
  user: User;
  token: string;
}

export interface Conta {
  id: number;
  nome: string;
  saldoValor: number;
  saldoMoeda: string;
  versao?: number;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export type StatusTransferencia =
  | 'INICIADA'
  | 'CONTA_ORIGEM_DEBITADA'
  | 'DEBITO_FALHOU'
  | 'CONCLUIDA'
  | 'CREDITO_FALHOU'
  | 'COMPENSADA'
  | 'COMPENSACAO_FALHOU'
  | string;

export interface Transferencia {
  id: number;
  contaOrigemId: number;
  contaDestinoId: number;
  valor: number;
  moeda: string;
  status: StatusTransferencia;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export interface LoginRequest {
  nome: string;
  senha: string;
}

export interface RegistrarClienteRequest {
  nome: string;
  senha: string;
}

export interface CriarContaRequest {
  nome: string;
  saldoInicial: number;
  moeda: string;
}

export interface IniciarTransferenciaCommand {
  contaOrigemId: number;
  contaDestinoId: number;
  valor: number;
  moeda: string;
}
