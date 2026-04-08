import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Reponse from './reponse';
import ReponseDeleteDialog from './reponse-delete-dialog';
import ReponseDetail from './reponse-detail';
import ReponseUpdate from './reponse-update';

const ReponseRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Reponse />} />
    <Route path="new" element={<ReponseUpdate />} />
    <Route path=":id">
      <Route index element={<ReponseDetail />} />
      <Route path="edit" element={<ReponseUpdate />} />
      <Route path="delete" element={<ReponseDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReponseRoutes;
