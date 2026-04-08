import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './evaluation.reducer';

export const EvaluationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const evaluationEntity = useAppSelector(state => state.evaluation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="evaluationDetailsHeading">
          <Translate contentKey="skillTestApp.evaluation.detail.title">Evaluation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.id}</dd>
          <dt>
            <span id="dateEvaluation">
              <Translate contentKey="skillTestApp.evaluation.dateEvaluation">Date Evaluation</Translate>
            </span>
          </dt>
          <dd>
            {evaluationEntity.dateEvaluation ? (
              <TextFormat value={evaluationEntity.dateEvaluation} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="skillTestApp.evaluation.status">Status</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.status}</dd>
          <dt>
            <span id="mode">
              <Translate contentKey="skillTestApp.evaluation.mode">Mode</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.mode}</dd>
          <dt>
            <span id="scoreTotal">
              <Translate contentKey="skillTestApp.evaluation.scoreTotal">Score Total</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.scoreTotal}</dd>
          <dt>
            <span id="remarques">
              <Translate contentKey="skillTestApp.evaluation.remarques">Remarques</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.remarques}</dd>
          <dt>
            <span id="statut">
              <Translate contentKey="skillTestApp.evaluation.statut">Statut</Translate>
            </span>
          </dt>
          <dd>{evaluationEntity.statut}</dd>
          <dt>
            <Translate contentKey="skillTestApp.evaluation.employee">Employee</Translate>
          </dt>
          <dd>{evaluationEntity.employee ? evaluationEntity.employee.nom : ''}</dd>
          <dt>
            <Translate contentKey="skillTestApp.evaluation.test">Test</Translate>
          </dt>
          <dd>{evaluationEntity.test ? evaluationEntity.test.titre : ''}</dd>
          <dt>
            <Translate contentKey="skillTestApp.evaluation.manager">Manager</Translate>
          </dt>
          <dd>{evaluationEntity.manager ? evaluationEntity.manager.nom : ''}</dd>
        </dl>
        <Button as={Link as any} to="/evaluation" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/evaluation/${evaluationEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EvaluationDetail;
