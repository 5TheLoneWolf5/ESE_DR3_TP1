import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router';
import App from './App';
import Layout from './layouts/dashboard';
import LoginPage from './pages/login';
import SignupPage from './pages/signup';
import DashboardPage from './pages/index';
import TransferirPage from './pages/transferir';
import ExtratoPage from './pages/extrato';
import ContasPage from './pages/contas';
import AdminContasCrudPage from './pages/admin-contas';
import AdminTransferenciasCrudPage from './pages/admin-transferencias';

const router = createBrowserRouter([
  {
    Component: App,
    children: [
      {
        path: '/login',
        Component: LoginPage,
      },
      {
        path: '/signup',
        Component: SignupPage,
      },
      {
        path: '/',
        Component: Layout,
        children: [
          {
            path: '',
            Component: DashboardPage,
          },
          {
            path: 'transferir',
            Component: TransferirPage,
          },
          {
            path: 'extrato',
            Component: ExtratoPage,
          },
          {
            path: 'contas',
            Component: ContasPage,
          },
          {
            path: 'admin/contas/:contaId?/*',
            Component: AdminContasCrudPage,
          },
          {
            path: 'admin/transferencias/:transferenciaId?/*',
            Component: AdminTransferenciasCrudPage,
          },
        ],
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);