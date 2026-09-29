import React, { useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  IconButton,
  Tooltip,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Snackbar,
  Alert,
  Button,
} from '@mui/material';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import ContactlessIcon from '@mui/icons-material/Contactless';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import { useAuth } from '../context/AuthContext';
import { Conta } from '../types';

interface AccountCardProps {
  onOpenNovaContaModal?: () => void;
}

export const AccountCard: React.FC<AccountCardProps> = ({ onOpenNovaContaModal }) => {
  const { user, activeConta, userContas, setActiveConta } = useAuth();
  const [showBalance, setShowBalance] = useState<boolean>(true);
  const [copySuccess, setCopySuccess] = useState<boolean>(false);

  const formatCurrency = (val: number, moeda: string = 'BRL') => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: moeda === 'BRL' ? 'BRL' : 'USD',
    }).format(val || 0);
  };

  const handleCopyAccountId = () => {
    if (activeConta) {
      navigator.clipboard.writeText(String(activeConta.id));
      setCopySuccess(true);
    }
  };

  if (!activeConta) {
    return (
      <Card
        sx={{
          p: 3,
          textAlign: 'center',
          borderRadius: 3,
          border: '1px dashed #cbd5e1',
          background: 'rgba(255,255,255,0.7)',
        }}
      >
        <AccountBalanceWalletIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 1 }} />
        <Typography variant="h6" gutterBottom>
          Nenhuma conta bancária ativa
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Abra uma conta bancária para começar a movimentar seu saldo e transferências.
        </Typography>
        {onOpenNovaContaModal && (
          <Button
            variant="contained"
            startIcon={<AddCircleOutlineIcon />}
            onClick={onOpenNovaContaModal}
          >
            Abrir Minha Primeira Conta
          </Button>
        )}
      </Card>
    );
  }

  return (
    <>
      <Card
        elevation={4}
        sx={{
          borderRadius: 4,
          background: 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 50%, #0284c7 100%)',
          color: '#ffffff',
          position: 'relative',
          overflow: 'hidden',
          boxShadow: '0 10px 25px -5px rgba(37, 99, 235, 0.4)',
          transition: 'transform 0.2s ease, box-shadow 0.2s ease',
          '&:hover': {
            boxShadow: '0 15px 30px -5px rgba(37, 99, 235, 0.5)',
          },
        }}
      >
        {/* Decoração de fundo sutil */}
        <Box
          sx={{
            position: 'absolute',
            top: -40,
            right: -40,
            width: 180,
            height: 180,
            borderRadius: '50%',
            background: 'rgba(255, 255, 255, 0.08)',
            pointerEvents: 'none',
          }}
        />

        <CardContent sx={{ p: { xs: 2.5, sm: 3.5 } }}>
          {/* Topo do Cartão: Banco, Ícone Contactless e Seleção de Conta */}
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              mb: 3,
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <AccountBalanceWalletIcon sx={{ fontSize: 28, color: '#93c5fd' }} />
              <Typography variant="h6" sx={{ fontWeight: 800, letterSpacing: 0.5 }}>
                FinTech Bank
              </Typography>
              <ContactlessIcon sx={{ ml: 1, fontSize: 24, opacity: 0.8 }} />
            </Box>

            {userContas.length > 1 && (
              <FormControl size="small" sx={{ minWidth: 150 }}>
                <InputLabel
                  id="select-active-conta-label"
                  sx={{ color: 'rgba(255,255,255,0.8)', fontSize: '0.8rem' }}
                >
                  Trocar Conta
                </InputLabel>
                <Select
                  labelId="select-active-conta-label"
                  value={activeConta.id}
                  label="Trocar Conta"
                  onChange={(e) => {
                    const selected = userContas.find((c) => c.id === Number(e.target.value));
                    if (selected) setActiveConta(selected);
                  }}
                  sx={{
                    color: '#fff',
                    '.MuiOutlinedInput-notchedOutline': {
                      borderColor: 'rgba(255, 255, 255, 0.4)',
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#fff',
                    },
                    '.MuiSvgIcon-root': { color: '#fff' },
                    fontSize: '0.85rem',
                    bgcolor: 'rgba(255,255,255,0.12)',
                  }}
                >
                  {userContas.map((c) => (
                    <MenuItem key={c.id} value={c.id}>
                      Conta #{c.id} ({c.saldoMoeda})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            )}
          </Box>

          {/* Saldo da Conta com toggle de visualização */}
          <Box sx={{ mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Typography
                variant="caption"
                sx={{
                  textTransform: 'uppercase',
                  letterSpacing: 1.2,
                  color: 'rgba(255, 255, 255, 0.75)',
                  fontWeight: 600,
                }}
              >
                Saldo Disponível
              </Typography>
              <Tooltip title={showBalance ? 'Ocultar saldo' : 'Mostrar saldo'}>
                <IconButton
                  size="small"
                  onClick={() => setShowBalance(!showBalance)}
                  sx={{ color: 'rgba(255, 255, 255, 0.8)', p: 0.5 }}
                >
                  {showBalance ? (
                    <VisibilityOffIcon fontSize="inherit" />
                  ) : (
                    <VisibilityIcon fontSize="inherit" />
                  )}
                </IconButton>
              </Tooltip>
            </Box>

            <Typography
              variant="h4"
              sx={{
                fontWeight: 800,
                letterSpacing: -0.5,
                mt: 0.5,
                fontSize: { xs: '1.75rem', sm: '2.2rem' },
              }}
            >
              {showBalance
                ? formatCurrency(activeConta.saldoValor, activeConta.saldoMoeda)
                : 'R$ ••••••'}
            </Typography>
          </Box>

          {/* Rodapé do Cartão: Número da Conta e Titular */}
          <Box
            sx={{
              display: 'flex',
              flexDirection: { xs: 'column', sm: 'row' },
              justifyContent: 'space-between',
              alignItems: { xs: 'flex-start', sm: 'flex-end' },
              pt: 2,
              borderTop: '1px solid rgba(255, 255, 255, 0.15)',
              gap: 1.5,
            }}
          >
            <Box>
              <Typography
                variant="caption"
                sx={{ color: 'rgba(255, 255, 255, 0.7)', display: 'block', mb: 0.2 }}
              >
                Número da Conta (ID)
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <Typography variant="body1" sx={{ fontWeight: 700, letterSpacing: 1 }}>
                  #{String(activeConta.id).padStart(4, '0')}
                </Typography>
                <Tooltip title="Copiar número da conta">
                  <IconButton
                    size="small"
                    onClick={handleCopyAccountId}
                    sx={{ color: 'rgba(255, 255, 255, 0.85)', p: 0.4 }}
                  >
                    <ContentCopyIcon sx={{ fontSize: '0.9rem' }} />
                  </IconButton>
                </Tooltip>
              </Box>
            </Box>

            <Box sx={{ textAlign: { xs: 'left', sm: 'right' } }}>
              <Typography
                variant="caption"
                sx={{ color: 'rgba(255, 255, 255, 0.7)', display: 'block', mb: 0.2 }}
              >
                Titular da Conta
              </Typography>
              <Typography
                variant="body1"
                sx={{
                  fontWeight: 700,
                  textTransform: 'uppercase',
                  letterSpacing: 0.5,
                }}
              >
                {activeConta.nome || user?.nome}
              </Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>

      <Snackbar
        open={copySuccess}
        autoHideDuration={2500}
        onClose={() => setCopySuccess(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setCopySuccess(false)} severity="success" sx={{ width: '100%' }}>
          Número da conta copiado para a área de transferência!
        </Alert>
      </Snackbar>
    </>
  );
};
