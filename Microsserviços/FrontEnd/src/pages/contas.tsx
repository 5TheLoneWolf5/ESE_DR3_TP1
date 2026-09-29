import React, { useState } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Tabs,
  Tab,
  Button,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  InputAdornment,
  CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import SendIcon from '@mui/icons-material/Send';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import RefreshIcon from '@mui/icons-material/Refresh';
import PeopleIcon from '@mui/icons-material/People';
import { useAuth } from '../context/AuthContext';
import { QuickActionModal, ActionModalType } from '../components/QuickActionModal';
import { Conta } from '../types';

export default function ContasPage() {
  const navigate = useNavigate();
  const {
    user,
    activeConta,
    userContas,
    allContas,
    setActiveConta,
    refreshContas,
    criarNovaConta,
  } = useAuth();

  const [tabIndex, setTabIndex] = useState<number>(0);
  const [modalType, setModalType] = useState<ActionModalType>(null);
  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [novaContaOpen, setNovaContaOpen] = useState<boolean>(false);
  const [saldoInicialNova, setSaldoInicialNova] = useState<string>('0');
  const [loadingNova, setLoadingNova] = useState<boolean>(false);
  const [errorNova, setErrorNova] = useState<string | null>(null);
  const [toast, setToast] = useState<{ open: boolean; message: string; severity: 'success' | 'error' | 'info' }>({
    open: false,
    message: '',
    severity: 'success',
  });

  const formatCurrency = (val: number, moeda: string = 'BRL') => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: moeda === 'BRL' ? 'BRL' : 'USD',
    }).format(val || 0);
  };

  const handleOpenActionModal = (type: ActionModalType) => {
    setModalType(type);
    setModalOpen(true);
  };

  const handleCriarConta = async () => {
    setErrorNova(null);
    const num = parseFloat(saldoInicialNova.replace(',', '.'));
    if (isNaN(num) || num < 0) {
      setErrorNova('Informe um saldo inicial válido (0 ou positivo).');
      return;
    }

    setLoadingNova(true);
    try {
      await criarNovaConta(num, 'BRL');
      setToast({
        open: true,
        message: 'Nova conta bancária aberta com sucesso!',
        severity: 'success',
      });
      setNovaContaOpen(false);
      setSaldoInicialNova('0');
    } catch (err: any) {
      setErrorNova(err.message || 'Erro ao abrir conta.');
    } finally {
      setLoadingNova(false);
    }
  };

  // Contas de outros clientes (Diretório do Banco)
  const contasOutros = allContas.filter(
    (c) => c.nome.toLowerCase() !== user?.nome.toLowerCase()
  );

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
            <AccountBalanceIcon color="primary" sx={{ fontSize: 32 }} />
            <Typography variant="h5" sx={{ fontWeight: 800, color: '#1e293b' }}>
              Contas Bancárias & Diretório
            </Typography>
          </Box>
          <Typography variant="body2" color="text.secondary">
            Gerencie suas contas correntes e consulte contas do banco para transferências.
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="contained"
            startIcon={<AddCircleIcon />}
            onClick={() => setNovaContaOpen(true)}
            sx={{ fontWeight: 700, textTransform: 'none' }}
          >
            Abrir Nova Conta
          </Button>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={() => refreshContas()}
          >
            Atualizar
          </Button>
        </Box>
      </Box>

      {/* Abas */}
      <Card elevation={1} sx={{ borderRadius: 3, border: '1px solid #e2e8f0', mb: 3 }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}>
          <Tabs value={tabIndex} onChange={(_, val) => setTabIndex(val)}>
            <Tab
              label={`Minhas Contas (${userContas.length})`}
              sx={{ fontWeight: 700, textTransform: 'none' }}
            />
            <Tab
              icon={<PeopleIcon sx={{ fontSize: 18 }} />}
              iconPosition="start"
              label={`Diretório do Banco (${contasOutros.length})`}
              sx={{ fontWeight: 700, textTransform: 'none' }}
            />
          </Tabs>
        </Box>

        {/* Aba 0: Minhas Contas */}
        {tabIndex === 0 && (
          <Box sx={{ p: 3 }}>
            <Grid container spacing={2.5}>
              {userContas.map((c: Conta) => {
                const isCurrentActive = activeConta?.id === c.id;

                return (
                  <Grid size={{ xs: 12, sm: 6, md: 4 }} key={c.id}>
                    <Card
                      elevation={isCurrentActive ? 3 : 1}
                      sx={{
                        borderRadius: 3,
                        border: isCurrentActive ? '2px solid #2563eb' : '1px solid #e2e8f0',
                        bgcolor: isCurrentActive ? '#f8faff' : '#ffffff',
                        position: 'relative',
                      }}
                    >
                      <CardContent sx={{ p: 2.5 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                          <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
                            Conta #{c.id}
                          </Typography>
                          {isCurrentActive ? (
                            <Chip
                              icon={<CheckCircleIcon sx={{ fontSize: '1rem !important' }} />}
                              label="Conta Ativa"
                              color="primary"
                              size="small"
                              sx={{ fontWeight: 700 }}
                            />
                          ) : (
                            <Button
                              size="small"
                              variant="outlined"
                              onClick={() => setActiveConta(c)}
                              sx={{ textTransform: 'none', py: 0.2, fontSize: '0.75rem' }}
                            >
                              Tornar Ativa
                            </Button>
                          )}
                        </Box>

                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 0.5 }}>
                          Titular: <strong>{c.nome}</strong>
                        </Typography>

                        <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                          Saldo Disponível
                        </Typography>
                        <Typography
                          variant="h5"
                          sx={{
                            fontWeight: 800,
                            color: 'primary.main',
                            mb: 2,
                            mt: 0.5,
                          }}
                        >
                          {formatCurrency(c.saldoValor, c.saldoMoeda)}
                        </Typography>

                        <Box sx={{ display: 'flex', gap: 1 }}>
                          <Button
                            fullWidth
                            size="small"
                            variant="outlined"
                            color="success"
                            startIcon={<ArrowDownwardIcon />}
                            onClick={() => {
                              setActiveConta(c);
                              handleOpenActionModal('depositar');
                            }}
                            sx={{ textTransform: 'none', fontSize: '0.8rem' }}
                          >
                            Depositar
                          </Button>
                          <Button
                            fullWidth
                            size="small"
                            variant="outlined"
                            color="error"
                            startIcon={<ArrowUpwardIcon />}
                            onClick={() => {
                              setActiveConta(c);
                              handleOpenActionModal('sacar');
                            }}
                            sx={{ textTransform: 'none', fontSize: '0.8rem' }}
                          >
                            Sacar
                          </Button>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
          </Box>
        )}

        {/* Aba 1: Diretório do Banco */}
        {tabIndex === 1 && (
          <TableContainer>
            <Table>
              <TableHead sx={{ bgcolor: '#f8fafc' }}>
                <TableRow>
                  <TableCell sx={{ fontWeight: 700 }}>ID da Conta</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Titular</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Moeda</TableCell>
                  <TableCell sx={{ fontWeight: 700 }} align="right">
                    Ação
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {contasOutros.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} align="center" sx={{ py: 6 }}>
                      <Typography variant="body1" color="text.secondary">
                        Nenhuma outra conta cadastrada no banco ainda. Crie outros usuários para
                        testar transferências entre contas!
                      </Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  contasOutros.map((c: Conta) => (
                    <TableRow key={c.id} hover>
                      <TableCell>
                        <Typography variant="body2" sx={{ fontWeight: 700 }}>
                          #{c.id}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>
                          {c.nome}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip label={c.saldoMoeda || 'BRL'} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell align="right">
                        <Button
                          variant="contained"
                          size="small"
                          color="primary"
                          startIcon={<SendIcon />}
                          onClick={() => {
                            navigate('/transferir', { state: { destinoContaId: c.id } });
                          }}
                          sx={{ textTransform: 'none', fontWeight: 600 }}
                        >
                          Transferir para esta conta
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Card>

      {/* Modal de Ações (Depositar / Sacar) */}
      <QuickActionModal
        type={modalType}
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSuccess={(msg) => setToast({ open: true, message: msg, severity: 'success' })}
      />

      {/* Dialog Abrir Nova Conta */}
      <Dialog open={novaContaOpen} onClose={() => setNovaContaOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Abrir Nova Subconta Bancária</DialogTitle>
        <DialogContent dividers>
          {errorNova && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {errorNova}
            </Alert>
          )}

          <Typography variant="body2" color="text.secondary" paragraph>
            A nova conta será criada em nome de <strong>{user?.nome}</strong> integrada com o
            microsserviço de Contas.
          </Typography>

          <TextField
            fullWidth
            label="Saldo Inicial"
            type="number"
            value={saldoInicialNova}
            onChange={(e) => setSaldoInicialNova(e.target.value)}
            InputProps={{
              startAdornment: <InputAdornment position="start">R$</InputAdornment>,
            }}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setNovaContaOpen(false)} disabled={loadingNova}>
            Cancelar
          </Button>
          <Button
            onClick={handleCriarConta}
            variant="contained"
            disabled={loadingNova}
            startIcon={loadingNova ? <CircularProgress size={18} /> : undefined}
          >
            {loadingNova ? 'Abrindo...' : 'Abrir Conta'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar feedback */}
      <Snackbar
        open={toast.open}
        autoHideDuration={4000}
        onClose={() => setToast((prev) => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setToast((prev) => ({ ...prev, open: false }))}
          severity={toast.severity}
          sx={{ width: '100%', fontWeight: 600 }}
        >
          {toast.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
