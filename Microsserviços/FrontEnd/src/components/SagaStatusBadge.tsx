import React from 'react';
import { Chip } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import HourglassEmptyIcon from '@mui/icons-material/HourglassEmpty';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import { StatusTransferencia } from '../types';

interface SagaStatusBadgeProps {
  status: StatusTransferencia;
  size?: 'small' | 'medium';
}

export const SagaStatusBadge: React.FC<SagaStatusBadgeProps> = ({ status, size = 'small' }) => {
  switch (status) {
    case 'CONCLUIDA':
      return (
        <Chip
          icon={<CheckCircleIcon sx={{ fontSize: '1rem !important' }} />}
          label="Concluída (Saga OK)"
          color="success"
          size={size}
          variant="filled"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'INICIADA':
      return (
        <Chip
          icon={<HourglassEmptyIcon sx={{ fontSize: '1rem !important' }} />}
          label="Iniciada"
          color="info"
          size={size}
          variant="outlined"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'CONTA_ORIGEM_DEBITADA':
      return (
        <Chip
          icon={<HourglassEmptyIcon sx={{ fontSize: '1rem !important' }} />}
          label="Origem Debitada"
          color="primary"
          size={size}
          variant="filled"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'DEBITO_FALHOU':
      return (
        <Chip
          icon={<ErrorOutlineIcon sx={{ fontSize: '1rem !important' }} />}
          label="Débito Falhou"
          color="error"
          size={size}
          variant="filled"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'CREDITO_FALHOU':
      return (
        <Chip
          icon={<WarningAmberIcon sx={{ fontSize: '1rem !important' }} />}
          label="Crédito Falhou"
          color="warning"
          size={size}
          variant="filled"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'COMPENSADA':
      return (
        <Chip
          icon={<WarningAmberIcon sx={{ fontSize: '1rem !important' }} />}
          label="Compensada (Estornada)"
          color="warning"
          size={size}
          variant="outlined"
          sx={{ fontWeight: 600 }}
        />
      );

    case 'COMPENSACAO_FALHOU':
      return (
        <Chip
          icon={<ErrorOutlineIcon sx={{ fontSize: '1rem !important' }} />}
          label="Compensação Falhou"
          color="error"
          size={size}
          variant="filled"
          sx={{ fontWeight: 600 }}
        />
      );

    default:
      return (
        <Chip
          label={status || 'Desconhecido'}
          size={size}
          variant="outlined"
          sx={{ fontWeight: 500 }}
        />
      );
  }
};
