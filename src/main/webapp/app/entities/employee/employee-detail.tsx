import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './employee.reducer';

export const EmployeeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const employeeEntity = useAppSelector(state => state.employee.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="employeeDetailsHeading">
          <Translate contentKey="skillTestApp.employee.detail.title">Employee</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.id}</dd>
          <dt>
            <span id="matricule">
              <Translate contentKey="skillTestApp.employee.matricule">Matricule</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.matricule}</dd>
          <dt>
            <span id="nom">
              <Translate contentKey="skillTestApp.employee.nom">Nom</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.nom}</dd>
          <dt>
            <span id="prenom">
              <Translate contentKey="skillTestApp.employee.prenom">Prenom</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.prenom}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="skillTestApp.employee.email">Email</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.email}</dd>
          <dt>
            <span id="telephone">
              <Translate contentKey="skillTestApp.employee.telephone">Telephone</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.telephone}</dd>
          <dt>
            <span id="dateEmbauche">
              <Translate contentKey="skillTestApp.employee.dateEmbauche">Date Embauche</Translate>
            </span>
          </dt>
          <dd>
            {employeeEntity.dateEmbauche ? (
              <TextFormat value={employeeEntity.dateEmbauche} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="role">
              <Translate contentKey="skillTestApp.employee.role">Role</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.role}</dd>
          <dt>
            <Translate contentKey="skillTestApp.employee.poste">Poste</Translate>
          </dt>
          <dd>{employeeEntity.poste ? employeeEntity.poste.intitule : ''}</dd>
        </dl>
        <Button as={Link as any} to="/employee" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/employee/${employeeEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EmployeeDetail;
