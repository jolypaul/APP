import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './poste.reducer';

export const PosteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const posteEntity = useAppSelector(state => state.poste.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="posteDetailsHeading">
          <Translate contentKey="skillTestApp.poste.detail.title">Poste</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{posteEntity.id}</dd>
          <dt>
            <span id="intitule">
              <Translate contentKey="skillTestApp.poste.intitule">Intitule</Translate>
            </span>
          </dt>
          <dd>{posteEntity.intitule}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="skillTestApp.poste.description">Description</Translate>
            </span>
          </dt>
          <dd>{posteEntity.description}</dd>
          <dt>
            <span id="niveauRequis">
              <Translate contentKey="skillTestApp.poste.niveauRequis">Niveau Requis</Translate>
            </span>
          </dt>
          <dd>{posteEntity.niveauRequis}</dd>
          <dt>
            <Translate contentKey="skillTestApp.poste.competences">Competences</Translate>
          </dt>
          <dd>
            {posteEntity.competenceses
              ? posteEntity.competenceses.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.nom}</a>
                    {posteEntity.competenceses && i === posteEntity.competenceses.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button as={Link as any} to="/poste" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/poste/${posteEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PosteDetail;
