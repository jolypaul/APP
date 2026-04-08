import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './test.reducer';

export const TestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const testEntity = useAppSelector(state => state.test.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="testDetailsHeading">
          <Translate contentKey="skillTestApp.test.detail.title">Test</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{testEntity.id}</dd>
          <dt>
            <span id="titre">
              <Translate contentKey="skillTestApp.test.titre">Titre</Translate>
            </span>
          </dt>
          <dd>{testEntity.titre}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="skillTestApp.test.description">Description</Translate>
            </span>
          </dt>
          <dd>{testEntity.description}</dd>
          <dt>
            <span id="mode">
              <Translate contentKey="skillTestApp.test.mode">Mode</Translate>
            </span>
          </dt>
          <dd>{testEntity.mode}</dd>
          <dt>
            <span id="duree">
              <Translate contentKey="skillTestApp.test.duree">Duree</Translate>
            </span>
          </dt>
          <dd>{testEntity.duree}</dd>
          <dt>
            <span id="dateCreation">
              <Translate contentKey="skillTestApp.test.dateCreation">Date Creation</Translate>
            </span>
          </dt>
          <dd>{testEntity.dateCreation ? <TextFormat value={testEntity.dateCreation} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="actif">
              <Translate contentKey="skillTestApp.test.actif">Actif</Translate>
            </span>
          </dt>
          <dd>{testEntity.actif ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="skillTestApp.test.competences">Competences</Translate>
          </dt>
          <dd>
            {testEntity.competenceses
              ? testEntity.competenceses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.nom}</a>
                    {testEntity.competenceses && i === testEntity.competenceses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/test" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/test/${testEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TestDetail;
