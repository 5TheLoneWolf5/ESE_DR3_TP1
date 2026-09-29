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
  Divider,
  Chip,
  Paper,
} from '@mui/material';
import { useNavigate, useLocation, Link } from 'react-router';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import FlashOnIcon from '@mui/icons-material/FlashOn';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [nome, setNome] = useState('');
  const [senha, setSenha] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const from = (location.state as any)?.from?.pathname || '/';

   const sx = {
                '& .MuiInputBase-input': {
                  color: "black",
                }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nome.trim() || !senha.trim()) {
      setError('Por favor, informe seu usuário e senha.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      await login(nome.trim(), senha);
      navigate(from, { replace: true });
    } catch (err: any) {
      console.error(err);
      setError(
        err.message || 'Erro ao efetuar login. Verifique se o microsserviço de Conta está ativo.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handlePreencherDemo = (demoUser: string, demoPass: string) => {
    setNome(demoUser);
    setSenha(demoPass);
    setError(null);
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
          maxWidth: 440,
          width: '100%',
          borderRadius: 4,
          overflow: 'hidden',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
          background: 'rgba(255, 255, 255, 0.98)',
        }}
      >
        {/* Banner Superior com Logo */}
        <Box
          sx={{
            py: 3.5,
            px: 3,
            background: 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%)',
            color: '#fff',
            textAlign: 'center',
          }}
        >
          <Box
            sx={{
              display: 'inline-flex',
              p: 1.5,
              borderRadius: '50%',
              bgcolor: 'rgba(255, 255, 255, 0.15)',
              mb: 1,
            }}
          >
            <AccountBalanceIcon sx={{ fontSize: 36, color: '#93c5fd' }} />
          </Box>
          <Typography variant="h5" sx={{ fontWeight: 800, letterSpacing: -0.5 }}>
            FinTech Bank
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.85, mt: 0.5 }}>
            Internet Banking Seguro e Distribuído
          </Typography>
        </Box>

        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, color: '#1e293b' }}>
            Acesse sua Conta
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            Entre com suas credenciais para gerenciar saldos e transferências.
          </Typography>

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
              margin="normal"
              required
              sx={sx}
              autoFocus
              placeholder="Ex: alice"
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
              margin="normal"
              sx={sx}
              required
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

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                mt: 3,
                mb: 2,
                py: 1.4,
                borderRadius: 2.5,
                fontWeight: 700,
                textTransform: 'none',
                fontSize: '1rem',
                background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
              }}
            >
              {loading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                'Entrar no Internet Banking'
              )}
            </Button>
          </Box>

          <Box sx={{ textAlign: 'center', mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              Ainda não tem conta no banco?{' '}
              <MuiLink
                component={Link}
                to="/signup"
                sx={{ fontWeight: 700, color: 'primary.main', textDecoration: 'none' }}
              >
                Abra sua conta grátis
              </MuiLink>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
