import { matchPath, useLocation } from 'react-router';
import { Crud } from '@toolpad/core/Crud';
import { transferenciasDataSource, Transferencia } from '../data/transferencias';

export default function TransferenciasCrudPage() {
  const pathname = useLocation().pathname;
  const transferenciaId = pathname
    ? matchPath('/transferencias/:transferenciaId/*', pathname)?.params.transferenciaId
    : null;

  return (
    <Crud<Transferencia>
      dataSourceCache={null}
      dataSource={transferenciasDataSource}
      rootPath="/transferencias"
      initialPageSize={25}
      defaultValues={{ itemCount: 1 }}
      pageTitles={{
        show: `Transferência ${transferenciaId}`,
        create: 'Iniciar Nova Transferência (Saga)',
        edit: `Transferência ${transferenciaId}`,
      }}
    />
  );
}
