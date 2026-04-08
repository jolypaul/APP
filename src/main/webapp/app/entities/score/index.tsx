import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Score from './score';
import ScoreDeleteDialog from './score-delete-dialog';
import ScoreDetail from './score-detail';
import ScoreUpdate from './score-update';

const ScoreRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Score />} />
    <Route path="new" element={<ScoreUpdate />} />
    <Route path=":id">
      <Route index element={<ScoreDetail />} />
      <Route path="edit" element={<ScoreUpdate />} />
      <Route path="delete" element={<ScoreDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ScoreRoutes;
