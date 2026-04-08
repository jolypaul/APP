import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

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
        <Route path="/employee/*" element={<Employee />} />
        <Route path="/poste/*" element={<Poste />} />
        <Route path="/competence/*" element={<Competence />} />
        <Route path="/test/*" element={<Test />} />
        <Route path="/question/*" element={<Question />} />
        <Route path="/reponse/*" element={<Reponse />} />
        <Route path="/evaluation/*" element={<Evaluation />} />
        <Route path="/score/*" element={<Score />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
