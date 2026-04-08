import competence from 'app/entities/competence/competence.reducer';
import employee from 'app/entities/employee/employee.reducer';
import evaluation from 'app/entities/evaluation/evaluation.reducer';
import poste from 'app/entities/poste/poste.reducer';
import question from 'app/entities/question/question.reducer';
import reponse from 'app/entities/reponse/reponse.reducer';
import score from 'app/entities/score/score.reducer';
import test from 'app/entities/test/test.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  employee,
  poste,
  competence,
  test,
  question,
  reponse,
  evaluation,
  score,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
