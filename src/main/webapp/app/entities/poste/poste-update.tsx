import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCompetences } from 'app/entities/competence/competence.reducer';
import { Level } from 'app/shared/model/enumerations/level.model';
import { mapIdList } from 'app/shared/util/entity-utils';

import { createEntity, getEntity, reset, updateEntity } from './poste.reducer';

export const PosteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const competences = useAppSelector(state => state.competence.entities);
  const posteEntity = useAppSelector(state => state.poste.entity);
  const loading = useAppSelector(state => state.poste.loading);
  const updating = useAppSelector(state => state.poste.updating);
  const updateSuccess = useAppSelector(state => state.poste.updateSuccess);
  const levelValues = Object.keys(Level);

  const handleClose = () => {
    navigate(`/poste${location.search}`);
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

    const entity = {
      ...posteEntity,
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
      ? {}
      : {
          niveauRequis: 'DEBUTANT',
          ...posteEntity,
          competenceses: posteEntity?.competenceses?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.poste.home.createOrEditLabel" data-cy="PosteCreateUpdateHeading">
            <Translate contentKey="skillTestApp.poste.home.createOrEditLabel">Create or edit a Poste</Translate>
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
                  id="poste-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.poste.intitule')}
                id="poste-intitule"
                name="intitule"
                data-cy="intitule"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.poste.description')}
                id="poste-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('skillTestApp.poste.niveauRequis')}
                id="poste-niveauRequis"
                name="niveauRequis"
                data-cy="niveauRequis"
                type="select"
              >
                {levelValues.map(level => (
                  <option value={level} key={level}>
                    {translate(`skillTestApp.Level.${level}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.poste.competences')}
                id="poste-competences"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/poste" replace variant="info">
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

export default PosteUpdate;
