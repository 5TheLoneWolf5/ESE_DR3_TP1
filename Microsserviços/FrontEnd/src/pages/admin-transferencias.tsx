import React from 'react';
import { matchPath, useLocation } from 'react-router';
import { Crud } from '@toolpad/core/Crud';
import { transferenciasDataSource, Transferencia } from '../data/transferencias';
import { Box, Typography } from '@mui/material';

export default function AdminTransferenciasCrudPage() {
  const pathname = useLocation().pathname;
  const transferenciaId = pathname
    ? matchPath('/admin/transferencias/:transferenciaId/*', pathname)?.params.transferenciaId
    : null;

  return (
    <Box sx={{ p: { xs: 2, sm: 3 } }}>
      <Box sx={{ mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 800 }}>
          Administração: CRUD de Transferências (Visão Técnica)
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Tabela bruta de visualização e manipulação direta de transferências via Toolpad.
        </Typography>
      </Box>

      <Crud<Transferencia>
        dataSourceCache={null}
        dataSource={transferenciasDataSource}
        rootPath="/admin/transferencias"
        initialPageSize={25}
        defaultValues={{ itemCount: 1 }}
        pageTitles={{
          show: `Transferência ${transferenciaId}`,
          create: 'Iniciar Nova Transferência (Saga)',
          edit: `Transferência ${transferenciaId}`,
        }}
      />
    </Box>
  );
}
