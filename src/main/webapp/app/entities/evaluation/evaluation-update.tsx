import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { getEntities as getTests } from 'app/entities/test/test.reducer';
import { EvaluationStatus } from 'app/shared/model/enumerations/evaluation-status.model';
import { Statut } from 'app/shared/model/enumerations/statut.model';
import { TestMode } from 'app/shared/model/enumerations/test-mode.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './evaluation.reducer';

export const EvaluationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employees = useAppSelector(state => state.employee.entities);
  const tests = useAppSelector(state => state.test.entities);
  const evaluationEntity = useAppSelector(state => state.evaluation.entity);
  const loading = useAppSelector(state => state.evaluation.loading);
  const updating = useAppSelector(state => state.evaluation.updating);
  const updateSuccess = useAppSelector(state => state.evaluation.updateSuccess);
  const evaluationStatusValues = Object.keys(EvaluationStatus);
  const testModeValues = Object.keys(TestMode);
  const statutValues = Object.keys(Statut);

  const handleClose = () => {
    navigate(`/evaluation${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEmployees({}));
    dispatch(getTests({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.dateEvaluation = convertDateTimeToServer(values.dateEvaluation);
    if (values.scoreTotal !== undefined && typeof values.scoreTotal !== 'number') {
      values.scoreTotal = Number(values.scoreTotal);
    }

    const entity = {
      ...evaluationEntity,
      ...values,
      employee: employees.find(it => it.id.toString() === values.employee?.toString()),
      test: tests.find(it => it.id.toString() === values.test?.toString()),
      manager: employees.find(it => it.id.toString() === values.manager?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          dateEvaluation: displayDefaultDateTime(),
        }
      : {
          status: 'EN_COURS',
          mode: 'CLASSIQUE',
          statut: 'CONFORME',
          ...evaluationEntity,
          dateEvaluation: convertDateTimeFromServer(evaluationEntity.dateEvaluation),
          employee: evaluationEntity?.employee?.id,
          test: evaluationEntity?.test?.id,
          manager: evaluationEntity?.manager?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.evaluation.home.createOrEditLabel" data-cy="EvaluationCreateUpdateHeading">
            <Translate contentKey="skillTestApp.evaluation.home.createOrEditLabel">Create or edit a Evaluation</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="evaluation-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.evaluation.dateEvaluation')}
                id="evaluation-dateEvaluation"
                name="dateEvaluation"
                data-cy="dateEvaluation"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.evaluation.status')}
                id="evaluation-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {evaluationStatusValues.map(evaluationStatus => (
                  <option value={evaluationStatus} key={evaluationStatus}>
                    {translate(`skillTestApp.EvaluationStatus.${evaluationStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.evaluation.mode')}
                id="evaluation-mode"
                name="mode"
                data-cy="mode"
                type="select"
              >
                {testModeValues.map(testMode => (
                  <option value={testMode} key={testMode}>
                    {translate(`skillTestApp.TestMode.${testMode}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.evaluation.scoreTotal')}
                id="evaluation-scoreTotal"
                name="scoreTotal"
                data-cy="scoreTotal"
                type="text"
              />
              <ValidatedField
                label={translate('skillTestApp.evaluation.remarques')}
                id="evaluation-remarques"
                name="remarques"
                data-cy="remarques"
                type="textarea"
              />
              <ValidatedField
                label={translate('skillTestApp.evaluation.statut')}
                id="evaluation-statut"
                name="statut"
                data-cy="statut"
                type="select"
              >
                {statutValues.map(statut => (
                  <option value={statut} key={statut}>
                    {translate(`skillTestApp.Statut.${statut}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="evaluation-employee"
                name="employee"
                data-cy="employee"
                label={translate('skillTestApp.evaluation.employee')}
                type="select"
              >
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nom}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="evaluation-test"
                name="test"
                data-cy="test"
                label={translate('skillTestApp.evaluation.test')}
                type="select"
              >
                <option value="" key="0" />
                {tests
                  ? tests.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.titre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="evaluation-manager"
                name="manager"
                data-cy="manager"
                label={translate('skillTestApp.evaluation.manager')}
                type="select"
              >
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nom}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/evaluation" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EvaluationUpdate;
