import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCompetences } from 'app/entities/competence/competence.reducer';
import { TestMode } from 'app/shared/model/enumerations/test-mode.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './test.reducer';

export const TestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const competences = useAppSelector(state => state.competence.entities);
  const testEntity = useAppSelector(state => state.test.entity);
  const loading = useAppSelector(state => state.test.loading);
  const updating = useAppSelector(state => state.test.updating);
  const updateSuccess = useAppSelector(state => state.test.updateSuccess);
  const testModeValues = Object.keys(TestMode);

  const handleClose = () => {
    navigate(`/test${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCompetences({}));
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
    if (values.duree !== undefined && typeof values.duree !== 'number') {
      values.duree = Number(values.duree);
    }
    values.dateCreation = convertDateTimeToServer(values.dateCreation);

    const entity = {
      ...testEntity,
      ...values,
      competenceses: mapIdList(values.competenceses),
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
          dateCreation: displayDefaultDateTime(),
        }
      : {
          mode: 'CLASSIQUE',
          ...testEntity,
          dateCreation: convertDateTimeFromServer(testEntity.dateCreation),
          competenceses: testEntity?.competenceses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.test.home.createOrEditLabel" data-cy="TestCreateUpdateHeading">
            <Translate contentKey="skillTestApp.test.home.createOrEditLabel">Create or edit a Test</Translate>
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
                  id="test-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.test.titre')}
                id="test-titre"
                name="titre"
                data-cy="titre"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.test.description')}
                id="test-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField label={translate('skillTestApp.test.mode')} id="test-mode" name="mode" data-cy="mode" type="select">
                {testModeValues.map(testMode => (
                  <option value={testMode} key={testMode}>
                    {translate(`skillTestApp.TestMode.${testMode}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label={translate('skillTestApp.test.duree')} id="test-duree" name="duree" data-cy="duree" type="text" />
              <ValidatedField
                label={translate('skillTestApp.test.dateCreation')}
                id="test-dateCreation"
                name="dateCreation"
                data-cy="dateCreation"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.test.actif')}
                id="test-actif"
                name="actif"
                data-cy="actif"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('skillTestApp.test.competences')}
                id="test-competences"
                data-cy="competences"
                type="select"
                multiple
                name="competenceses"
              >
                <option value="" key="0" />
                {competences
                  ? competences.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nom}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/test" replace variant="info">
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

export default TestUpdate;
