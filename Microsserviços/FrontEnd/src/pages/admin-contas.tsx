import React from 'react';
import { matchPath, useLocation } from 'react-router';
import { Crud } from '@toolpad/core/Crud';
import { contasDataSource, Conta } from '../data/contas';
import { Box, Typography } from '@mui/material';

export default function AdminContasCrudPage() {
  const contaId = useLocation().pathname
    ? matchPath('/admin/contas/:contaId/*', useLocation().pathname)?.params.contaId
    : null;

  return (
    <Box sx={{ p: { xs: 2, sm: 3 } }}>
      <Box sx={{ mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 800 }}>
          Administração: CRUD de Contas (Visão Técnica)
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Tabela bruta de visualização e manipulação direta de contas via Toolpad.
        </Typography>
      </Box>

      <Crud<Conta>
        dataSourceCache={null}
        dataSource={contasDataSource}
        rootPath="/admin/contas"
        initialPageSize={25}
        defaultValues={{ itemCount: 1 }}
        pageTitles={{
          show: `Conta ${contaId}`,
          create: 'Nova conta',
          edit: `Conta ${contaId} - Alterar Saldo`,
        }}
      />
    </Box>
  );
}
