import React, { useMemo } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import type { Navigation } from '@toolpad/core/AppProvider';
import HomeIcon from '@mui/icons-material/Home';
import SendIcon from '@mui/icons-material/Send';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import PersonIcon from '@mui/icons-material/Person';
import SyncAltIcon from '@mui/icons-material/SyncAlt';
import { AuthProvider, useAuth } from './context/AuthContext';
import theme from './theme';

const NAVIGATION: Navigation = [
  {
    kind: 'header',
    title: 'Internet Banking',
  },
  {
    title: 'Visão Geral',
    icon: <HomeIcon />,
  },
  {
    segment: 'transferir',
    title: 'Transferir (Saga)',
    icon: <SendIcon />,
  },
  {
    segment: 'extrato',
    title: 'Extrato',
    icon: <ReceiptLongIcon />,
  },
  {
    segment: 'contas',
    title: 'Minhas Contas & Contatos',
    icon: <AccountBalanceIcon />,
  },
  {
    kind: 'divider',
  },
  {
    kind: 'header',
    title: 'Visão Técnica (CRUD)',
  },
  {
    segment: 'admin/contas',
    title: 'CRUD Contas',
    icon: <PersonIcon />,
    pattern: 'admin/contas{/:contaId}*',
  },
  {
    segment: 'admin/transferencias',
    title: 'CRUD Transferências',
    icon: <SyncAltIcon />,
    pattern: 'admin/transferencias{/:transferenciaId}*',
  },
];

const BRANDING = {
  title: 'FinTech Banking',
  logo: <AccountBalanceIcon sx={{ color: 'primary.main', fontSize: 26 }} />,
  homeUrl: '/',
};

function AppWithSession() {
  const navigate = useNavigate();
  const { user, activeConta, logout } = useAuth();

  const session = useMemo(() => {
    if (!user) return null;
    return {
      user: {
        id: String(user.id),
        name: user.nome,
        email: activeConta
          ? `Conta #${activeConta.id} • Saldo: R$ ${activeConta.saldoValor.toFixed(2)}`
          : 'Cliente FinTech Bank',
      },
    };
  }, [user, activeConta]);

  const authentication = useMemo(() => {
    return {
      signIn: () => {
        navigate('/login');
      },
      signOut: () => {
        logout();
        navigate('/login');
      },
    };
  }, [logout, navigate]);

  return (
    <ReactRouterAppProvider
      theme={theme}
      navigation={NAVIGATION}
      branding={BRANDING}
      session={session}
      authentication={authentication}
    >
      <Outlet />
    </ReactRouterAppProvider>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppWithSession />
    </AuthProvider>
  );
}