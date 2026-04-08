import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './question.reducer';

export const QuestionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const questionEntity = useAppSelector(state => state.question.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="questionDetailsHeading">
          <Translate contentKey="skillTestApp.question.detail.title">Question</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{questionEntity.id}</dd>
          <dt>
            <span id="enonce">
              <Translate contentKey="skillTestApp.question.enonce">Enonce</Translate>
            </span>
          </dt>
          <dd>{questionEntity.enonce}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="skillTestApp.question.type">Type</Translate>
            </span>
          </dt>
          <dd>{questionEntity.type}</dd>
          <dt>
            <span id="niveau">
              <Translate contentKey="skillTestApp.question.niveau">Niveau</Translate>
            </span>
          </dt>
          <dd>{questionEntity.niveau}</dd>
          <dt>
            <span id="points">
              <Translate contentKey="skillTestApp.question.points">Points</Translate>
            </span>
          </dt>
          <dd>{questionEntity.points}</dd>
          <dt>
            <span id="choixMultiple">
              <Translate contentKey="skillTestApp.question.choixMultiple">Choix Multiple</Translate>
            </span>
          </dt>
          <dd>{questionEntity.choixMultiple}</dd>
          <dt>
            <span id="reponseAttendue">
              <Translate contentKey="skillTestApp.question.reponseAttendue">Reponse Attendue</Translate>
            </span>
          </dt>
          <dd>{questionEntity.reponseAttendue}</dd>
          <dt>
            <Translate contentKey="skillTestApp.question.test">Test</Translate>
          </dt>
          <dd>{questionEntity.test ? questionEntity.test.titre : ''}</dd>
        </dl>
        <Button as={Link as any} to="/question" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/question/${questionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuestionDetail;
