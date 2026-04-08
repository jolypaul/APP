import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPostes } from 'app/entities/poste/poste.reducer';
import { getEntities as getTests } from 'app/entities/test/test.reducer';
import { Level } from 'app/shared/model/enumerations/level.model';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './competence.reducer';

export const CompetenceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const postes = useAppSelector(state => state.poste.entities);
  const tests = useAppSelector(state => state.test.entities);
  const competenceEntity = useAppSelector(state => state.competence.entity);
  const loading = useAppSelector(state => state.competence.loading);
  const updating = useAppSelector(state => state.competence.updating);
  const updateSuccess = useAppSelector(state => state.competence.updateSuccess);
  const levelValues = Object.keys(Level);

  const handleClose = () => {
    navigate(`/competence${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPostes({}));
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

    const entity = {
      ...competenceEntity,
      ...values,
      posteses: mapIdList(values.posteses),
      testses: mapIdList(values.testses),
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
          niveauAttendu: 'DEBUTANT',
          ...competenceEntity,
          posteses: competenceEntity?.posteses?.map(e => e.id.toString()),
          testses: competenceEntity?.testses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.competence.home.createOrEditLabel" data-cy="CompetenceCreateUpdateHeading">
            <Translate contentKey="skillTestApp.competence.home.createOrEditLabel">Create or edit a Competence</Translate>
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
                  id="competence-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.competence.nom')}
                id="competence-nom"
                name="nom"
                data-cy="nom"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.competence.description')}
                id="competence-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('skillTestApp.competence.categorie')}
                id="competence-categorie"
                name="categorie"
                data-cy="categorie"
                type="text"
              />
              <ValidatedField
                label={translate('skillTestApp.competence.niveauAttendu')}
                id="competence-niveauAttendu"
                name="niveauAttendu"
                data-cy="niveauAttendu"
                type="select"
              >
                {levelValues.map(level => (
                  <option value={level} key={level}>
                    {translate(`skillTestApp.Level.${level}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.competence.postes')}
                id="competence-postes"
                data-cy="postes"
                type="select"
                multiple
                name="posteses"
              >
                <option value="" key="0" />
                {postes
                  ? postes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.intitule}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.competence.tests')}
                id="competence-tests"
                data-cy="tests"
                type="select"
                multiple
                name="testses"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/competence" replace variant="info">
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

export default CompetenceUpdate;
