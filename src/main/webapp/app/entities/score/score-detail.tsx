import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './score.reducer';

export const ScoreDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const scoreEntity = useAppSelector(state => state.score.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="scoreDetailsHeading">
          <Translate contentKey="skillTestApp.score.detail.title">Score</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{scoreEntity.id}</dd>
          <dt>
            <span id="valeur">
              <Translate contentKey="skillTestApp.score.valeur">Valeur</Translate>
            </span>
          </dt>
          <dd>{scoreEntity.valeur}</dd>
          <dt>
            <span id="pourcentage">
              <Translate contentKey="skillTestApp.score.pourcentage">Pourcentage</Translate>
            </span>
          </dt>
          <dd>{scoreEntity.pourcentage}</dd>
          <dt>
            <span id="statut">
              <Translate contentKey="skillTestApp.score.statut">Statut</Translate>
            </span>
          </dt>
          <dd>{scoreEntity.statut}</dd>
          <dt>
            <span id="dateCalcul">
              <Translate contentKey="skillTestApp.score.dateCalcul">Date Calcul</Translate>
            </span>
          </dt>
          <dd>{scoreEntity.dateCalcul ? <TextFormat value={scoreEntity.dateCalcul} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="skillTestApp.score.evaluation">Evaluation</Translate>
          </dt>
          <dd>{scoreEntity.evaluation ? scoreEntity.evaluation.id : ''}</dd>
          <dt>
            <Translate contentKey="skillTestApp.score.competence">Competence</Translate>
          </dt>
          <dd>{scoreEntity.competence ? scoreEntity.competence.nom : ''}</dd>
        </dl>
        <Button as={Link as any} to="/score" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/score/${scoreEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ScoreDetail;
