import React, { useState } from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Tooltip,
  Divider,
  Snackbar,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
} from '@mui/material';
import { useNavigate } from 'react-router';
import SendIcon from '@mui/icons-material/Send';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import RefreshIcon from '@mui/icons-material/Refresh';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import NorthEastIcon from '@mui/icons-material/NorthEast';
import SouthWestIcon from '@mui/icons-material/SouthWest';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import SwapHorizIcon from '@mui/icons-material/SwapHoriz';
import { useAuth } from '../context/AuthContext';
import { AccountCard } from '../components/AccountCard';
import { QuickActionModal, ActionModalType } from '../components/QuickActionModal';
import { SagaStatusBadge } from '../components/SagaStatusBadge';
import { Transferencia } from '../types';

export default function DashboardPage() {
  const navigate = useNavigate();
  const {
    user,
    activeConta,
    allTransferencias,
    refreshContas,
    refreshTransferencias,
  } = useAuth();

  const [modalType, setModalType] = useState<ActionModalType>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'info' | 'error' }>({
    open: false,
    message: '',
    severity: 'success',
  });

  const handleOpenModal = (type: ActionModalType) => {
    setModalType(type);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setModalType(null);
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      await Promise.all([refreshContas(), refreshTransferencias()]);
      setSnackbar({
        open: true,
        message: 'Dados atualizados com os microsserviços!',
        severity: 'info',
      });
    } catch {
      setSnackbar({
        open: true,
        message: 'Erro ao atualizar dados.',
        severity: 'error',
      });
    } finally {
      setRefreshing(false);
    }
  };

  // Transações relacionadas à conta ativa
  const transferenciasDaConta = activeConta
    ? allTransferencias.filter(
        (t) => t.contaOrigemId === activeConta.id || t.contaDestinoId === activeConta.id
      )
    : [];

  // Transações recentes (máximo 6)
  const transacoesRecentes = transferenciasDaConta.slice(0, 6);

  // Totais de entradas e saídas
  const totalEntradas = transferenciasDaConta
    .filter((t) => t.contaDestinoId === activeConta?.id && t.status === 'CONCLUIDA')
    .reduce((acc, t) => acc + Number(t.valor), 0);

  const totalSaidas = transferenciasDaConta
    .filter((t) => t.contaOrigemId === activeConta?.id && t.status === 'CONCLUIDA')
    .reduce((acc, t) => acc + Number(t.valor), 0);

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(val || 0);
  };

  return (
    <Box sx={{ p: { xs: 2, sm: 3 }, maxWidth: 1200, mx: 'auto' }}>
      {/* Cabeçalho de Boas-Vindas */}
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 3,
          flexWrap: 'wrap',
          gap: 1.5,
        }}
      >
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 800, color: '#1e293b' }}>
            Olá, {user?.nome || 'Cliente'}! 👋
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Bem-vindo ao seu Internet Banking com arquitetura orientada a eventos e Saga.
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Atualizar dados bancários">
            <span>
              <Button
                variant="outlined"
                size="small"
                startIcon={
                  refreshing ? (
                    <CircularProgress size={16} color="inherit" />
                  ) : (
                    <RefreshIcon />
                  )
                }
                onClick={handleRefresh}
                disabled={refreshing}
              >
                Atualizar
              </Button>
            </span>
          </Tooltip>
        </Box>
      </Box>

      {/* Grid Principal: Cartão da Conta + Resumo Rápido */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {/* Cartão da Conta Principal */}
        <Grid size={{ xs: 12, md: 7 }}>
          <AccountCard onOpenNovaContaModal={() => handleOpenModal('novaConta')} />
        </Grid>

        {/* Resumo de Entradas e Saídas */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Grid container spacing={2} sx={{ height: '100%' }}>
            <Grid size={{ xs: 12, sm: 6, md: 12 }}>
              <Card
                elevation={1}
                sx={{
                  borderRadius: 3,
                  border: '1px solid #e2e8f0',
                  height: '100%',
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
                  <Box
                    sx={{
                      p: 1.5,
                      borderRadius: '50%',
                      bgcolor: '#ecfdf5',
                      color: '#059669',
                      display: 'flex',
                    }}
                  >
                    <TrendingUpIcon sx={{ fontSize: 28 }} />
                  </Box>
                  <Box>
                    <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                      Entradas Recebidas (Sagas OK)
                    </Typography>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: '#059669' }}>
                      + {formatCurrency(totalEntradas)}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 12 }}>
              <Card
                elevation={1}
                sx={{
                  borderRadius: 3,
                  border: '1px solid #e2e8f0',
                  height: '100%',
                  display: 'flex',
                  alignItems: 'center',
                }}
              >
                <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
                  <Box
                    sx={{
                      p: 1.5,
                      borderRadius: '50%',
                      bgcolor: '#fef2f2',
                      color: '#dc2626',
                      display: 'flex',
                    }}
                  >
                    <TrendingDownIcon sx={{ fontSize: 28 }} />
                  </Box>
                  <Box>
                    <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                      Saídas Enviadas (Sagas OK)
                    </Typography>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: '#dc2626' }}>
                      - {formatCurrency(totalSaidas)}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Grid>
      </Grid>

      {/* Barra de Ações Rápidas */}
      <Card elevation={1} sx={{ borderRadius: 3, mb: 4, border: '1px solid #e2e8f0' }}>
        <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
          <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 2, color: 'text.secondary' }}>
            AÇÕES BANCÁRIAS RÁPIDAS
          </Typography>

          <Grid container spacing={2}>
            <Grid size={{ xs: 6, sm: 3 }}>
              <Button
                fullWidth
                variant="contained"
                startIcon={<SendIcon />}
                onClick={() => handleOpenModal('transferir')}
                sx={{
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 700,
                  textTransform: 'none',
                  background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
                }}
              >
                Transferir (Saga)
              </Button>
            </Grid>

            <Grid size={{ xs: 6, sm: 3 }}>
              <Button
                fullWidth
                variant="outlined"
                color="success"
                startIcon={<ArrowDownwardIcon />}
                onClick={() => handleOpenModal('depositar')}
                sx={{
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 700,
                  textTransform: 'none',
                  borderWidth: 2,
                  '&:hover': { borderWidth: 2 },
                }}
              >
                Depositar Saldo
              </Button>
            </Grid>

            <Grid size={{ xs: 6, sm: 3 }}>
              <Button
                fullWidth
                variant="outlined"
                color="error"
                startIcon={<ArrowUpwardIcon />}
                onClick={() => handleOpenModal('sacar')}
                sx={{
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 700,
                  textTransform: 'none',
                  borderWidth: 2,
                  '&:hover': { borderWidth: 2 },
                }}
              >
                Sacar / Pagar
              </Button>
            </Grid>

            <Grid size={{ xs: 6, sm: 3 }}>
              <Button
                fullWidth
                variant="outlined"
                color="primary"
                startIcon={<ReceiptLongIcon />}
                onClick={() => navigate('/extrato')}
                sx={{
                  py: 1.5,
                  borderRadius: 2.5,
                  fontWeight: 700,
                  textTransform: 'none',
                  borderWidth: 2,
                  '&:hover': { borderWidth: 2 },
                }}
              >
                Ver Extrato
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Extrato Recente da Conta Ativa */}
      <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <CardContent sx={{ p: { xs: 2.5, sm: 3 } }}>
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              mb: 2,
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <SwapHorizIcon color="primary" />
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#1e293b' }}>
                Atividades Recentes da Conta #{activeConta?.id}
              </Typography>
            </Box>

            {transferenciasDaConta.length > 0 && (
              <Button
                size="small"
                variant="text"
                onClick={() => navigate('/extrato')}
                sx={{ fontWeight: 700, textTransform: 'none' }}
              >
                Ver todas ({transferenciasDaConta.length}) →
              </Button>
            )}
          </Box>

          <Divider sx={{ mb: 1.5 }} />

          {transacoesRecentes.length === 0 ? (
            <Box sx={{ py: 5, textAlign: 'center' }}>
              <SwapHorizIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 1 }} />
              <Typography variant="body1" color="text.secondary" sx={{ fontWeight: 500 }}>
                Nenhuma transferência registrada nesta conta ainda.
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, mb: 2 }}>
                Faça uma transferência via Saga ou deposite fundos para começar!
              </Typography>
              <Button
                variant="contained"
                size="small"
                startIcon={<SendIcon />}
                onClick={() => handleOpenModal('transferir')}
              >
                Fazer Primeira Transferência
              </Button>
            </Box>
          ) : (
            <List disablePadding>
              {transacoesRecentes.map((t: Transferencia, index) => {
                const isOrigem = t.contaOrigemId === activeConta?.id;

                return (
                  <React.Fragment key={t.id}>
                    <ListItem
                      sx={{
                        py: 1.5,
                        px: 1,
                        borderRadius: 2,
                        '&:hover': { bgcolor: 'action.hover' },
                      }}
                    >
                      <ListItemIcon sx={{ minWidth: 44 }}>
                        <Box
                          sx={{
                            p: 1,
                            borderRadius: '50%',
                            bgcolor: isOrigem ? '#fef2f2' : '#ecfdf5',
                            color: isOrigem ? '#dc2626' : '#059669',
                            display: 'flex',
                          }}
                        >
                          {isOrigem ? <NorthEastIcon fontSize="small" /> : <SouthWestIcon fontSize="small" />}
                        </Box>
                      </ListItemIcon>

                      <ListItemText
                        primary={
                          <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                            {isOrigem
                              ? `Transferência Enviada para Conta #${t.contaDestinoId}`
                              : `Transferência Recebida da Conta #${t.contaOrigemId}`}
                          </Typography>
                        }
                        secondary={
                          <Typography variant="caption" color="text.secondary">
                            Transação #{t.id} • {t.moeda || 'BRL'}
                          </Typography>
                        }
                      />

                      <Box sx={{ textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 0.5 }}>
                        <Typography
                          variant="subtitle1"
                          sx={{
                            fontWeight: 800,
                            color: isOrigem ? '#dc2626' : '#059669',
                          }}
                        >
                          {isOrigem ? '-' : '+'} {formatCurrency(t.valor)}
                        </Typography>
                        <SagaStatusBadge status={t.status} size="small" />
                      </Box>
                    </ListItem>
                    {index < transacoesRecentes.length - 1 && <Divider component="li" />}
                  </React.Fragment>
                );
              })}
            </List>
          )}
        </CardContent>
      </Card>

      {/* Modal de Ações Rápidas */}
      <QuickActionModal
        type={modalType}
        open={modalOpen}
        onClose={handleCloseModal}
        onSuccess={(msg) => setSnackbar({ open: true, message: msg, severity: 'success' })}
      />

      {/* Toast de Notificação */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
          severity={snackbar.severity}
          sx={{ width: '100%', fontWeight: 600 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
