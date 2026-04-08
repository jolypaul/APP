import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getTests } from 'app/entities/test/test.reducer';
import { Level } from 'app/shared/model/enumerations/level.model';
import { QuestionType } from 'app/shared/model/enumerations/question-type.model';

import { createEntity, getEntity, reset, updateEntity } from './question.reducer';

export const QuestionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tests = useAppSelector(state => state.test.entities);
  const questionEntity = useAppSelector(state => state.question.entity);
  const loading = useAppSelector(state => state.question.loading);
  const updating = useAppSelector(state => state.question.updating);
  const updateSuccess = useAppSelector(state => state.question.updateSuccess);
  const questionTypeValues = Object.keys(QuestionType);
  const levelValues = Object.keys(Level);

  const handleClose = () => {
    navigate(`/question${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.points !== undefined && typeof values.points !== 'number') {
      values.points = Number(values.points);
    }

    const entity = {
      ...questionEntity,
      ...values,
      test: tests.find(it => it.id.toString() === values.test?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          type: 'QCM',
          niveau: 'DEBUTANT',
          ...questionEntity,
          test: questionEntity?.test?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.question.home.createOrEditLabel" data-cy="QuestionCreateUpdateHeading">
            <Translate contentKey="skillTestApp.question.home.createOrEditLabel">Create or edit a Question</Translate>
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
                  id="question-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.question.enonce')}
                id="question-enonce"
                name="enonce"
                data-cy="enonce"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('skillTestApp.question.type')} id="question-type" name="type" data-cy="type" type="select">
                {questionTypeValues.map(questionType => (
                  <option value={questionType} key={questionType}>
                    {translate(`skillTestApp.QuestionType.${questionType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.question.niveau')}
                id="question-niveau"
                name="niveau"
                data-cy="niveau"
                type="select"
              >
                {levelValues.map(level => (
                  <option value={level} key={level}>
                    {translate(`skillTestApp.Level.${level}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.question.points')}
                id="question-points"
                name="points"
                data-cy="points"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.question.choixMultiple')}
                id="question-choixMultiple"
                name="choixMultiple"
                data-cy="choixMultiple"
                type="textarea"
              />
              <ValidatedField
                label={translate('skillTestApp.question.reponseAttendue')}
                id="question-reponseAttendue"
                name="reponseAttendue"
                data-cy="reponseAttendue"
                type="textarea"
              />
              <ValidatedField id="question-test" name="test" data-cy="test" label={translate('skillTestApp.question.test')} type="select">
                <option value="" key="0" />
                {tests
                  ? tests.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.titre}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/question" replace variant="info">
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

export default QuestionUpdate;
