import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PrivateRoute from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import Competence from './competence';
import Employee from './employee';
import Evaluation from './evaluation';
import Poste from './poste';
import Question from './question';
import Reponse from './reponse';
import Score from './score';
import Test from './test';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route
          path="/employee/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.RH]}>
              <Employee />
            </PrivateRoute>
          }
        />
        <Route
          path="/poste/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.RH]}>
              <Poste />
            </PrivateRoute>
          }
        />
        <Route
          path="/competence/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.EXPERT]}>
              <Competence />
            </PrivateRoute>
          }
        />
        <Route
          path="/test/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Test />
            </PrivateRoute>
          }
        />
        <Route
          path="/question/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER]}>
              <Question />
            </PrivateRoute>
          }
        />
        <Route
          path="/reponse/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.EXPERT, Authority.EMPLOYEE]}>
              <Reponse />
            </PrivateRoute>
          }
        />
        <Route
          path="/evaluation/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.MANAGER, Authority.EXPERT]}>
              <Evaluation />
            </PrivateRoute>
          }
        />
        <Route
          path="/score/*"
          element={
            <PrivateRoute hasAnyAuthorities={[Authority.ADMIN, Authority.RH]}>
              <Score />
            </PrivateRoute>
          }
        />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
