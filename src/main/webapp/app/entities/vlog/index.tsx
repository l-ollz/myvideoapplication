import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Vlog from './vlog';
import VlogDetail from './vlog-detail';
import VlogUpdate from './vlog-update';
import VlogDeleteDialog from './vlog-delete-dialog';

const VlogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Vlog />} />
    <Route path="new" element={<VlogUpdate />} />
    <Route path=":id">
      <Route index element={<VlogDetail />} />
      <Route path="edit" element={<VlogUpdate />} />
      <Route path="delete" element={<VlogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VlogRoutes;
