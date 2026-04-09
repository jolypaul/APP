import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';

import { getEntity } from './reponse.reducer';

export const ReponseDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const reponseEntity = useAppSelector(state => state.reponse.entity);
  const account = useAppSelector(state => state.authentication.account);
  const authorities: string[] = account?.authorities ?? [];
  const isManager = hasAnyAuthority(authorities, [Authority.MANAGER]) && !hasAnyAuthority(authorities, [Authority.ADMIN]);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reponseDetailsHeading">
          <Translate contentKey="skillTestApp.reponse.detail.title">Reponse</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{reponseEntity.id}</dd>
          <dt>
            <span id="contenu">
              <Translate contentKey="skillTestApp.reponse.contenu">Contenu</Translate>
            </span>
          </dt>
          <dd>{reponseEntity.contenu}</dd>
          {!isManager && (
            <>
              <dt>
                <span id="estCorrecte">
                  <Translate contentKey="skillTestApp.reponse.estCorrecte">Est Correcte</Translate>
                </span>
              </dt>
              <dd>{reponseEntity.estCorrecte ? 'true' : 'false'}</dd>
            </>
          )}
          <dt>
            <span id="dateReponse">
              <Translate contentKey="skillTestApp.reponse.dateReponse">Date Reponse</Translate>
            </span>
          </dt>
          <dd>
            {reponseEntity.dateReponse ? <TextFormat value={reponseEntity.dateReponse} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="commentaireManager">
              <Translate contentKey="skillTestApp.reponse.commentaireManager">Commentaire Manager</Translate>
            </span>
          </dt>
          <dd>{reponseEntity.commentaireManager}</dd>
          <dt>
            <Translate contentKey="skillTestApp.reponse.question">Question</Translate>
          </dt>
          <dd>{reponseEntity.question ? reponseEntity.question.enonce : ''}</dd>
          <dt>
            <Translate contentKey="skillTestApp.reponse.evaluation">Evaluation</Translate>
          </dt>
          <dd>{reponseEntity.evaluation ? reponseEntity.evaluation.id : ''}</dd>
        </dl>
        <Button as={Link as any} to="/reponse" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/reponse/${reponseEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReponseDetail;
