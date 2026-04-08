import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Poste from './poste';
import PosteDeleteDialog from './poste-delete-dialog';
import PosteDetail from './poste-detail';
import PosteUpdate from './poste-update';

const PosteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Poste />} />
    <Route path="new" element={<PosteUpdate />} />
    <Route path=":id">
      <Route index element={<PosteDetail />} />
      <Route path="edit" element={<PosteUpdate />} />
      <Route path="delete" element={<PosteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PosteRoutes;
