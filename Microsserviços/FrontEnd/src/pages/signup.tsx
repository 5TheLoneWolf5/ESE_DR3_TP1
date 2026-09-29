import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Link as MuiLink,
  InputAdornment,
  IconButton,
  Chip,
  Paper,
} from '@mui/material';
import { useNavigate, Link } from 'react-router';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import { useAuth } from '../context/AuthContext';

export default function SignupPage() {
  const navigate = useNavigate();
  const { signup } = useAuth();

  const [nome, setNome] = useState('');
  const [senha, setSenha] = useState('');
  const [confirmSenha, setConfirmSenha] = useState('');
  const [saldoInicial, setSaldoInicial] = useState('500');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const sx = {
                '& .MuiInputBase-input': {
                  color: "black",
                }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!nome.trim()) {
      setError('Por favor, informe seu nome de usuário.');
      return;
    }

    if (senha.length < 3) {
      setError('A senha deve ter no mínimo 3 caracteres.');
      return;
    }

    if (senha !== confirmSenha) {
      setError('As senhas não coincidem.');
      return;
    }

    const saldoNum = parseFloat(saldoInicial.replace(',', '.'));
    if (isNaN(saldoNum) || saldoNum < 0) {
      setError('Por favor, informe um saldo inicial válido (0 ou maior).');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      await signup(nome.trim(), senha, saldoNum);
      navigate('/', { replace: true });
    } catch (err: any) {
      console.error(err);
      setError(
        err.message || 'Erro ao registrar cliente e conta. Verifique os microsserviços.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #172554 100%)',
        p: 2,
      }}
    >
      <Card
        elevation={10}
        sx={{
          maxWidth: 480,
          width: '100%',
          borderRadius: 4,
          overflow: 'hidden',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
          background: 'rgba(255, 255, 255, 0.98)',
        }}
      >
        {/* Banner Superior */}
        <Box
          sx={{
            py: 3,
            px: 3,
            background: 'linear-gradient(135deg, #059669 0%, #10b981 100%)',
            color: '#fff',
            textAlign: 'center',
          }}
        >
          <Box
            sx={{
              display: 'inline-flex',
              p: 1.2,
              borderRadius: '50%',
              bgcolor: 'rgba(255, 255, 255, 0.15)',
              mb: 1,
            }}
          >
            <AccountBalanceIcon sx={{ fontSize: 32, color: '#d1fae5' }} />
          </Box>
          <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: -0.5 }}>
            Abra sua Conta Grátis
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.9, mt: 0.5 }}>
            Cadastre-se e comece a movimentar suas transferências Saga
          </Typography>
        </Box>

        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          {error && (
            <Alert severity="error" sx={{ mb: 2.5, borderRadius: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Nome do Titular / Usuário"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              margin="dense"
              required
              sx={sx}
              autoFocus
              placeholder="Ex: Carlos Silva"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonOutlineIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              fullWidth
              label="Senha"
              type={showPassword ? 'text' : 'password'}
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              margin="dense"
              required
              sx={sx}
              placeholder="Crie uma senha"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlinedIcon color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                      size="small"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              fullWidth
              label="Confirmar Senha"
              type={showPassword ? 'text' : 'password'}
              value={confirmSenha}
              onChange={(e) => setConfirmSenha(e.target.value)}
              margin="dense"
              required
              sx={sx}
              placeholder="Repita sua senha"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlinedIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              fullWidth
              label="Depósito Inicial (Saldo da Conta)"
              type="number"
              value={saldoInicial}
              onChange={(e) => setSaldoInicial(e.target.value)}
              margin="dense"
              sx={sx}
              helperText="Saldo inicial creditado na sua conta corrente para testes"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <AttachMoneyIcon color="action" />
                  </InputAdornment>
                ),
              }}
            />

            {/* Quick chips para saldo inicial */}
            <Box sx={{ display: 'flex', gap: 1, my: 1.5, flexWrap: 'wrap' }}>
              {[0, 100, 500, 1000, 2500].map((v) => (
                <Chip
                  key={v}
                  label={v === 0 ? 'R$ 0 (Sem saldo)' : `R$ ${v}`}
                  size="small"
                  clickable
                  sx={{ color: "black" }}
                  variant={saldoInicial === String(v) ? 'filled' : 'outlined'}
                  color={saldoInicial === String(v) ? 'success' : 'default'}
                  onClick={() => setSaldoInicial(String(v))}
                />
              ))}
            </Box>

            <Paper
              variant="outlined"
              sx={{
                p: 1.5,
                my: 2,
                borderRadius: 2,
                bgcolor: '#f0fdf4',
                borderColor: '#bbf7d0',
                display: 'flex',
                gap: 1.2,
                alignItems: 'flex-start',
              }}
            >
              <CheckCircleOutlineIcon sx={{ color: 'success.main', fontSize: 20, mt: 0.2 }} />
              <Typography variant="caption" sx={{ color: '#166534', lineHeight: 1.4 }}>
                Ao cadastrar, o sistema registra seu cliente no microsserviço de autenticação e cria
                automaticamente sua conta corrente integrada no banco de dados.
              </Typography>
            </Paper>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                mt: 1,
                mb: 2,
                py: 1.4,
                borderRadius: 2.5,
                fontWeight: 700,
                textTransform: 'none',
                fontSize: '1rem',
                background: 'linear-gradient(135deg, #059669 0%, #047857 100%)',
              }}
            >
              {loading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                'Criar Minha Conta Bancária'
              )}
            </Button>
          </Box>

          <Box sx={{ textAlign: 'center', mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Já possui uma conta cadastrada?{' '}
              <MuiLink
                component={Link}
                to="/login"
                sx={{ fontWeight: 700, color: 'primary.main', textDecoration: 'none' }}
              >
                Faça login
              </MuiLink>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
