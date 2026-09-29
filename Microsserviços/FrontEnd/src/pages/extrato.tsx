import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Tabs,
  Tab,
  TextField,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import RefreshIcon from '@mui/icons-material/Refresh';
import NorthEastIcon from '@mui/icons-material/NorthEast';
import SouthWestIcon from '@mui/icons-material/SouthWest';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { useAuth } from '../context/AuthContext';
import { SagaStatusBadge } from '../components/SagaStatusBadge';
import { Transferencia } from '../types';

export default function ExtratoPage() {
  const {
    activeConta,
    userContas,
    setActiveConta,
    allTransferencias,
    refreshContas,
    refreshTransferencias,
  } = useAuth();

  const [tabIndex, setTabIndex] = useState<number>(0); // 0 = Todas, 1 = Entradas, 2 = Saídas
  const [statusFilter, setStatusFilter] = useState<string>('TODOS');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [refreshing, setRefreshing] = useState<boolean>(false);

  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      await Promise.all([refreshContas(), refreshTransferencias()]);
    } finally {
      setRefreshing(false);
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(val || 0);
  };

  // Filtragem das transferências pertencentes à conta ativa
  const transferenciasDaConta = activeConta
    ? allTransferencias.filter(
        (t) => t.contaOrigemId === activeConta.id || t.contaDestinoId === activeConta.id
      )
    : [];

  const totalEntradas = transferenciasDaConta
    .filter((t) => t.contaDestinoId === activeConta?.id && t.status === 'CONCLUIDA')
    .reduce((acc, t) => acc + Number(t.valor), 0);

  const totalSaidas = transferenciasDaConta
    .filter((t) => t.contaOrigemId === activeConta?.id && t.status === 'CONCLUIDA')
    .reduce((acc, t) => acc + Number(t.valor), 0);

  // Aplica filtros de tipo (Aba), status e busca textual
  const transferenciasFiltradas = transferenciasDaConta.filter((t) => {
    const isOrigem = t.contaOrigemId === activeConta?.id;
    const isDestino = t.contaDestinoId === activeConta?.id;

    // Filtro por Aba
    if (tabIndex === 1 && !isDestino) return false;
    if (tabIndex === 2 && !isOrigem) return false;

    // Filtro por Status
    if (statusFilter !== 'TODOS' && t.status !== statusFilter) return false;

    // Filtro por termo de busca
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      const matchId = String(t.id).includes(term);
      const matchOrigem = String(t.contaOrigemId).includes(term);
      const matchDestino = String(t.contaDestinoId).includes(term);
      const matchValor = String(t.valor).includes(term);
      if (!matchId && !matchOrigem && !matchDestino && !matchValor) return false;
    }

    return true;
  });

  return (
    <Box sx={{ p: { xs: 2, sm: 3 }, maxWidth: 1200, mx: 'auto' }}>
      {/* Cabeçalho */}
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
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <ReceiptLongIcon color="primary" sx={{ fontSize: 32 }} />
            <Typography variant="h5" sx={{ fontWeight: 800, color: '#1e293b' }}>
              Extrato da Conta Bancária
            </Typography>
          </Box>
          <Typography variant="body2" color="text.secondary">
            Histórico completo de transações e movimentações da conta.
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          {userContas.length > 1 && (
            <FormControl size="small" sx={{ minWidth: 160 }}>
              <InputLabel id="filtro-conta-label">Visualizar Conta</InputLabel>
              <Select
                labelId="filtro-conta-label"
                value={activeConta?.id || ''}
                label="Visualizar Conta"
                onChange={(e) => {
                  const sel = userContas.find((c) => c.id === Number(e.target.value));
                  if (sel) setActiveConta(sel);
                }}
              >
                {userContas.map((c) => (
                  <MenuItem key={c.id} value={c.id}>
                    Conta #{c.id}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}

          <Button
            variant="outlined"
            size="small"
            startIcon={refreshing ? <CircularProgress size={16} /> : <RefreshIcon />}
            onClick={handleRefresh}
            disabled={refreshing}
          >
            Atualizar
          </Button>
        </Box>
      </Box>

      {/* Cards de Resumo */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Box
                sx={{
                  p: 1.5,
                  borderRadius: '50%',
                  bgcolor: '#eff6ff',
                  color: '#2563eb',
                  display: 'flex',
                }}
              >
                <AccountBalanceWalletIcon sx={{ fontSize: 28 }} />
              </Box>
              <Box>
                <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600 }}>
                  Saldo em Conta #{activeConta?.id}
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800, color: 'primary.main' }}>
                  {formatCurrency(activeConta?.saldoValor || 0)}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, sm: 4 }}>
          <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
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
                  Total Recebido (Entradas)
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800, color: '#059669' }}>
                  + {formatCurrency(totalEntradas)}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, sm: 4 }}>
          <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
            <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
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
                  Total Enviado (Saídas)
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 800, color: '#dc2626' }}>
                  - {formatCurrency(totalSaidas)}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Filtros e Tabela */}
      <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}>
          <Tabs value={tabIndex} onChange={(_, val) => setTabIndex(val)}>
            <Tab label="Todas as Operações" sx={{ fontWeight: 700, textTransform: 'none' }} />
            <Tab label="Entradas (+)" sx={{ fontWeight: 700, textTransform: 'none' }} />
            <Tab label="Saídas (-)" sx={{ fontWeight: 700, textTransform: 'none' }} />
          </Tabs>
        </Box>

        <Box sx={{ p: 2, display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
          <TextField
            size="small"
            placeholder="Buscar por ID ou Conta..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" />
                </InputAdornment>
              ),
            }}
            sx={{ flexGrow: 1, minWidth: 220 }}
          />

          <FormControl size="small" sx={{ minWidth: 180 }}>
            <InputLabel id="status-filter-label">Status da Saga</InputLabel>
            <Select
              labelId="status-filter-label"
              value={statusFilter}
              label="Status da Saga"
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <MenuItem value="TODOS">Todos os Status</MenuItem>
              <MenuItem value="CONCLUIDA">Concluída</MenuItem>
              <MenuItem value="INICIADA">Iniciada</MenuItem>
              <MenuItem value="CONTA_ORIGEM_DEBITADA">Origem Debitada</MenuItem>
              <MenuItem value="DEBITO_FALHOU">Débito Falhou</MenuItem>
              <MenuItem value="COMPENSADA">Compensada</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* Tabela de Extrato */}
        <TableContainer>
          <Table>
            <TableHead sx={{ bgcolor: '#f8fafc' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>Tipo</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>ID</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Origem / Destino</TableCell>
                <TableCell sx={{ fontWeight: 700 }} align="right">
                  Valor
                </TableCell>
                <TableCell sx={{ fontWeight: 700 }} align="center">
                  Status da Saga
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {transferenciasFiltradas.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 6 }}>
                    <Typography variant="body1" color="text.secondary">
                      Nenhuma transação encontrada com os filtros selecionados.
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                transferenciasFiltradas.map((t: Transferencia) => {
                  const isOrigem = t.contaOrigemId === activeConta?.id;

                  return (
                    <TableRow key={t.id} hover>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Box
                            sx={{
                              p: 0.8,
                              borderRadius: '50%',
                              bgcolor: isOrigem ? '#fef2f2' : '#ecfdf5',
                              color: isOrigem ? '#dc2626' : '#059669',
                              display: 'flex',
                            }}
                          >
                            {isOrigem ? (
                              <NorthEastIcon sx={{ fontSize: 16 }} />
                            ) : (
                              <SouthWestIcon sx={{ fontSize: 16 }} />
                            )}
                          </Box>
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {isOrigem ? 'Enviada' : 'Recebida'}
                          </Typography>
                        </Box>
                      </TableCell>

                      <TableCell>
                        <Typography variant="body2" sx={{ fontWeight: 600, color: 'text.secondary' }}>
                          #{t.id}
                        </Typography>
                      </TableCell>

                      <TableCell>
                        {isOrigem ? (
                          <Typography variant="body2">
                            Para Conta <strong>#{t.contaDestinoId}</strong>
                          </Typography>
                        ) : (
                          <Typography variant="body2">
                            De Conta <strong>#{t.contaOrigemId}</strong>
                          </Typography>
                        )}
                      </TableCell>

                      <TableCell align="right">
                        <Typography
                          variant="subtitle2"
                          sx={{
                            fontWeight: 800,
                            color: isOrigem ? '#dc2626' : '#059669',
                          }}
                        >
                          {isOrigem ? '-' : '+'} {formatCurrency(t.valor)}
                        </Typography>
                      </TableCell>

                      <TableCell align="center">
                        <SagaStatusBadge status={t.status} size="small" />
                      </TableCell>
                    </TableRow>
                  );
                })
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>
    </Box>
  );
}
