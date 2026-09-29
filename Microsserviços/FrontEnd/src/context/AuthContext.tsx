import React, { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import { User, Conta, Transferencia } from '../types';
import { api, decodeJwt } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  activeConta: Conta | null;
  userContas: Conta[];
  allContas: Conta[];
  allTransferencias: Transferencia[];
  loading: boolean;
  login: (nome: string, senha: string) => Promise<void>;
  signup: (nome: string, senha: string, saldoInicial?: number) => Promise<void>;
  logout: () => void;
  setActiveConta: (conta: Conta) => void;
  refreshContas: () => Promise<void>;
  refreshTransferencias: () => Promise<void>;
  depositar: (valor: number, contaId?: number) => Promise<void>;
  sacar: (valor: number, contaId?: number) => Promise<void>;
  criarNovaConta: (saldoInicial?: number, moeda?: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = 'fintech_token';
const USER_KEY = 'fintech_user';
const ACTIVE_CONTA_ID_KEY = 'fintech_active_conta_id';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem(USER_KEY);
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem(TOKEN_KEY);
  });
  const [activeConta, setActiveContaState] = useState<Conta | null>(null);
  const [userContas, setUserContas] = useState<Conta[]>([]);
  const [allContas, setAllContas] = useState<Conta[]>([]);
  const [allTransferencias, setAllTransferencias] = useState<Transferencia[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const didInitialize = useRef(false);

  const setActiveConta = (conta: Conta) => {
    setActiveContaState(conta);
    localStorage.setItem(ACTIVE_CONTA_ID_KEY, String(conta.id));
  };

  const refreshContas = useCallback(async () => {
    if (!token && !localStorage.getItem(TOKEN_KEY)) {
      return;
    }
    try {
      const contas = await api.contas.listar();
      setAllContas(contas);

      const currentUser = user || (localStorage.getItem(USER_KEY) ? JSON.parse(localStorage.getItem(USER_KEY)!) : null);
      if (currentUser) {
        const userOwned = contas.filter(
          (c) => c.nome.toLowerCase() === currentUser.nome.toLowerCase()
        );
        setUserContas(userOwned);

        const savedActiveId = localStorage.getItem(ACTIVE_CONTA_ID_KEY);
        if (savedActiveId) {
          const match = userOwned.find((c) => c.id === Number(savedActiveId));
          if (match) {
            setActiveContaState(match);
            return;
          }
        }

        if (userOwned.length > 0) {
          setActiveContaState((prev) => {
            if (prev) {
              const updated = userOwned.find((c) => c.id === prev.id);
              if (updated) return updated;
            }
            return userOwned[0];
          });
        } else {
          setActiveContaState(null);
        }
      }
    } catch (err) {
      console.error('Erro ao atualizar contas:', err);
    }
  }, [token, user]);

  const refreshTransferencias = useCallback(async () => {
    if (!token && !localStorage.getItem(TOKEN_KEY)) {
      return;
    }
    try {
      const lista = await api.transferencias.listar();
      // Ordena por id decrescente para as mais recentes virem primeiro
      const ordenadas = [...lista].sort((a, b) => b.id - a.id);
      setAllTransferencias(ordenadas);
    } catch (err) {
      console.error('Erro ao buscar transferências:', err);
    }
  }, [token]);

  // Inicialização ao carregar
  useEffect(() => {
    if (didInitialize.current) return;
    didInitialize.current = true;

    const init = async () => {
      setLoading(true);
      const savedToken = localStorage.getItem(TOKEN_KEY);
      const savedUserStr = localStorage.getItem(USER_KEY);

      if (savedToken && savedUserStr) {
        try {
          const parsedUser: User = JSON.parse(savedUserStr);
          setUser(parsedUser);
          setToken(savedToken);
          await refreshContas();
          await refreshTransferencias();
        } catch (e) {
          console.error('Erro ao restaurar sessão:', e);
          localStorage.removeItem(TOKEN_KEY);
          localStorage.removeItem(USER_KEY);
          setUser(null);
          setToken(null);
        }
      }
      setLoading(false);
    };

    init();
  }, [refreshContas, refreshTransferencias]);

  const login = async (nome: string, senha: string) => {
    setLoading(true);
    try {
      const res = await api.auth.login({ nome, senha });
      setToken(res.token);
      setUser(res.user);
      localStorage.setItem(TOKEN_KEY, res.token);
      localStorage.setItem(USER_KEY, JSON.stringify(res.user));

      // Busca contas e seleciona ou cria se não existir
      const contas = await api.contas.listar(res.token);
      setAllContas(contas);
      let userOwned = contas.filter(
        (c) => c.nome.toLowerCase() === res.user.nome.toLowerCase()
      );

      // Se o usuário não tiver conta bancária ainda, cria automaticamente uma para ele
      if (userOwned.length === 0) {
        await api.contas.criar(
          { nome: res.user.nome, saldoInicial: 1000, moeda: 'BRL' },
          res.token
        );
        const contasAtualizadas = await api.contas.listar(res.token);
        setAllContas(contasAtualizadas);
        userOwned = contasAtualizadas.filter(
          (c) => c.nome.toLowerCase() === res.user.nome.toLowerCase()
        );
      }

      setUserContas(userOwned);
      if (userOwned.length > 0) {
        setActiveContaState(userOwned[0]);
        localStorage.setItem(ACTIVE_CONTA_ID_KEY, String(userOwned[0].id));
      }

      await refreshTransferencias();
    } finally {
      setLoading(false);
    }
  };

  const signup = async (nome: string, senha: string, saldoInicial: number = 500) => {
    setLoading(true);
    try {
      // 1. Cadastra o cliente no microsserviço de Conta/Auth
      await api.auth.registrar({ nome, senha });

      // 2. Faz o login para obter token de autenticação
      const loginRes = await api.auth.login({ nome, senha });
      setToken(loginRes.token);
      setUser(loginRes.user);
      localStorage.setItem(TOKEN_KEY, loginRes.token);
      localStorage.setItem(USER_KEY, JSON.stringify(loginRes.user));

      // 3. Cria a conta corrente inicial integrada com saldo
      await api.contas.criar(
        { nome, saldoInicial: Number(saldoInicial) || 0, moeda: 'BRL' },
        loginRes.token
      );

      // 4. Carrega as contas criadas
      const contas = await api.contas.listar(loginRes.token);
      setAllContas(contas);
      const userOwned = contas.filter(
        (c) => c.nome.toLowerCase() === nome.toLowerCase()
      );
      setUserContas(userOwned);
      if (userOwned.length > 0) {
        setActiveContaState(userOwned[0]);
        localStorage.setItem(ACTIVE_CONTA_ID_KEY, String(userOwned[0].id));
      }

      await refreshTransferencias();
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(ACTIVE_CONTA_ID_KEY);
    setUser(null);
    setToken(null);
    setActiveContaState(null);
    setUserContas([]);
    setAllContas([]);
    setAllTransferencias([]);
  };

  const depositar = async (valor: number, contaId?: number) => {
    const id = contaId || activeConta?.id;
    if (!id) throw new Error('Nenhuma conta selecionada para depósito.');
    await api.contas.creditar(id, valor, token);
    await refreshContas();
  };

  const sacar = async (valor: number, contaId?: number) => {
    const id = contaId || activeConta?.id;
    if (!id) throw new Error('Nenhuma conta selecionada para saque.');
    await api.contas.debitar(id, valor, token);
    await refreshContas();
  };

  const criarNovaConta = async (saldoInicial: number = 0, moeda: string = 'BRL') => {
    if (!user) throw new Error('Usuário não autenticado.');
    await api.contas.criar({ nome: user.nome, saldoInicial, moeda }, token);
    await refreshContas();
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        activeConta,
        userContas,
        allContas,
        allTransferencias,
        loading,
        login,
        signup,
        logout,
        setActiveConta,
        refreshContas,
        refreshTransferencias,
        depositar,
        sacar,
        criarNovaConta,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
};
