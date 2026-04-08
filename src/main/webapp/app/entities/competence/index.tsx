import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Competence from './competence';
import CompetenceDeleteDialog from './competence-delete-dialog';
import CompetenceDetail from './competence-detail';
import CompetenceUpdate from './competence-update';

const CompetenceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Competence />} />
    <Route path="new" element={<CompetenceUpdate />} />
    <Route path=":id">
      <Route index element={<CompetenceDetail />} />
      <Route path="edit" element={<CompetenceUpdate />} />
      <Route path="delete" element={<CompetenceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CompetenceRoutes;
