import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Typography,
  Box,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  InputAdornment,
} from '@mui/material';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import SendIcon from '@mui/icons-material/Send';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { Conta } from '../types';

export type ActionModalType = 'depositar' | 'sacar' | 'transferir' | 'novaConta' | null;

interface QuickActionModalProps {
  type: ActionModalType;
  open: boolean;
  onClose: () => void;
  onSuccess?: (msg: string) => void;
}

export const QuickActionModal: React.FC<QuickActionModalProps> = ({
  type,
  open,
  onClose,
  onSuccess,
}) => {
  const {
    activeConta,
    allContas,
    depositar,
    sacar,
    criarNovaConta,
    refreshContas,
    refreshTransferencias,
  } = useAuth();

  const [valor, setValor] = useState<string>('');
  const [destinoId, setDestinoId] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const resetForm = () => {
    setValor('');
    setDestinoId('');
    setError(null);
    setLoading(false);
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  const handleConfirm = async () => {
    setError(null);
    const numValor = parseFloat(valor.replace(',', '.'));

    if (isNaN(numValor) || numValor <= 0) {
      setError('Por favor, informe um valor positivo válido.');
      return;
    }

    if (!activeConta) {
      setError('Nenhuma conta ativa selecionada.');
      return;
    }

    setLoading(true);
    try {
      if (type === 'depositar') {
        await depositar(numValor);
        onSuccess?.(`Depósito de R$ ${numValor.toFixed(2)} efetuado com sucesso!`);
        handleClose();
      } else if (type === 'sacar') {
        if (numValor > activeConta.saldoValor) {
          setError(
            `Saldo insuficiente. Seu saldo atual é de R$ ${activeConta.saldoValor.toFixed(2)}.`
          );
          setLoading(false);
          return;
        }
        await sacar(numValor);
        onSuccess?.(`Saque de R$ ${numValor.toFixed(2)} efetuado com sucesso!`);
        handleClose();
      } else if (type === 'transferir') {
        const destIdNum = Number(destinoId);
        if (!destIdNum || destIdNum <= 0) {
          setError('Por favor, selecione ou informe uma conta de destino válida.');
          setLoading(false);
          return;
        }
        if (destIdNum === activeConta.id) {
          setError('A conta de destino não pode ser a mesma conta de origem.');
          setLoading(false);
          return;
        }
        if (numValor > activeConta.saldoValor) {
          setError(
            `Saldo insuficiente. Seu saldo atual é de R$ ${activeConta.saldoValor.toFixed(2)}.`
          );
          setLoading(false);
          return;
        }

        const res = await api.transferencias.iniciar({
          contaOrigemId: activeConta.id,
          contaDestinoId: destIdNum,
          valor: numValor,
          moeda: activeConta.saldoMoeda || 'BRL',
        });

        await refreshContas();
        await refreshTransferencias();

        onSuccess?.(
          `Transferência de R$ ${numValor.toFixed(2)} para Conta #${destIdNum} iniciada com status: ${res.status}!`
        );
        handleClose();
      } else if (type === 'novaConta') {
        await criarNovaConta(numValor, 'BRL');
        onSuccess?.(`Nova conta aberta com sucesso com saldo inicial de R$ ${numValor.toFixed(2)}!`);
        handleClose();
      }
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'Erro ao realizar operação.');
    } finally {
      setLoading(false);
    }
  };

  const getTitle = () => {
    switch (type) {
      case 'depositar':
        return 'Depositar Saldo em Conta';
      case 'sacar':
        return 'Sacar / Pagar';
      case 'transferir':
        return 'Transferência entre Contas (Saga)';
      case 'novaConta':
        return 'Abrir Nova Conta Bancária';
      default:
        return '';
    }
  };

  const getIcon = () => {
    switch (type) {
      case 'depositar':
        return <ArrowDownwardIcon sx={{ color: 'success.main' }} />;
      case 'sacar':
        return <ArrowUpwardIcon sx={{ color: 'error.main' }} />;
      case 'transferir':
        return <SendIcon sx={{ color: 'primary.main' }} />;
      case 'novaConta':
        return <AddCircleIcon sx={{ color: 'primary.main' }} />;
      default:
        return null;
    }
  };

  // Contas disponíveis para destino (excluindo a ativa)
  const contasDestinoDisponiveis = allContas.filter((c) => c.id !== activeConta?.id);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1.5, pb: 1 }}>
        {getIcon()}
        <Typography variant="h6" sx={{ fontWeight: 700 }}>
          {getTitle()}
        </Typography>
      </DialogTitle>

      <DialogContent dividers>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {/* Informações da Conta Atual */}
        {activeConta && type !== 'novaConta' && (
          <Box
            sx={{
              p: 2,
              mb: 2.5,
              borderRadius: 2,
              bgcolor: 'action.hover',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Box>
              <Typography variant="caption" color="text.secondary">
                Conta de Origem
              </Typography>
              <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                Conta #{activeConta.id} ({activeConta.nome})
              </Typography>
            </Box>
            <Box sx={{ textAlign: 'right' }}>
              <Typography variant="caption" color="text.secondary">
                Saldo Atual
              </Typography>
              <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'primary.main' }}>
                R$ {activeConta.saldoValor.toFixed(2)}
              </Typography>
            </Box>
          </Box>
        )}

        {/* Se for transferência: escolher Conta de Destino */}
        {type === 'transferir' && (
          <Box sx={{ mb: 2.5 }}>
            <FormControl fullWidth size="medium">
              <InputLabel id="destino-select-label">Conta de Destino</InputLabel>
              <Select
                labelId="destino-select-label"
                value={destinoId}
                label="Conta de Destino"
                onChange={(e) => setDestinoId(e.target.value)}
              >
                {contasDestinoDisponiveis.map((c: Conta) => (
                  <MenuItem key={c.id} value={c.id}>
                    Conta #{c.id} - {c.nome} (Saldo atual: R$ {c.saldoValor.toFixed(2)})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 0.8 }}>
              Ou digite o ID da conta de destino diretamente abaixo:
            </Typography>
            <TextField
              sx={{ mt: 1 }}
              fullWidth
              size="small"
              placeholder="Ex: 2"
              type="number"
              value={destinoId}
              onChange={(e) => setDestinoId(e.target.value)}
              label="ID da Conta Destino"
            />
          </Box>
        )}

        {/* Campo de Valor */}
        <TextField
          fullWidth
          label={type === 'novaConta' ? 'Saldo Inicial' : 'Valor'}
          type="number"
          value={valor}
          onChange={(e) => setValor(e.target.value)}
          placeholder="0,00"
          InputProps={{
            startAdornment: <InputAdornment position="start">R$</InputAdornment>,
          }}
          helperText={
            type === 'sacar' || type === 'transferir'
              ? `Máximo disponível: R$ ${activeConta?.saldoValor.toFixed(2)}`
              : 'Informe a quantia em reais'
          }
        />

        {/* Atalhos de valores rápidos */}
        {type !== 'novaConta' && (
          <Box sx={{ display: 'flex', gap: 1, mt: 1.5, flexWrap: 'wrap' }}>
            {[20, 50, 100, 200, 500].map((quickVal) => (
              <Chip
                key={quickVal}
                label={`+ R$ ${quickVal}`}
                clickable
                variant="outlined"
                size="small"
                onClick={() => setValor(String(quickVal))}
              />
            ))}
            {(type === 'sacar' || type === 'transferir') && activeConta && (
              <Chip
                label="Usar Saldo Total"
                clickable
                color="primary"
                variant="outlined"
                size="small"
                onClick={() => setValor(String(activeConta.saldoValor))}
              />
            )}
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ p: 2 }}>
        <Button onClick={handleClose} disabled={loading} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={handleConfirm}
          variant="contained"
          disabled={loading || !valor}
          startIcon={loading ? <CircularProgress size={18} color="inherit" /> : undefined}
          color={type === 'sacar' ? 'error' : type === 'depositar' ? 'success' : 'primary'}
        >
          {loading ? 'Processando...' : 'Confirmar Operação'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
