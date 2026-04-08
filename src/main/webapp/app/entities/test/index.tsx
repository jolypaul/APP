import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Test from './test';
import TestDeleteDialog from './test-delete-dialog';
import TestDetail from './test-detail';
import TestUpdate from './test-update';

const TestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Test />} />
    <Route path="new" element={<TestUpdate />} />
    <Route path=":id">
      <Route index element={<TestDetail />} />
      <Route path="edit" element={<TestUpdate />} />
      <Route path="delete" element={<TestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TestRoutes;
