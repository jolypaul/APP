import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCompetences } from 'app/entities/competence/competence.reducer';
import { getEntities as getEvaluations } from 'app/entities/evaluation/evaluation.reducer';
import { Statut } from 'app/shared/model/enumerations/statut.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './score.reducer';

export const ScoreUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const evaluations = useAppSelector(state => state.evaluation.entities);
  const competences = useAppSelector(state => state.competence.entities);
  const scoreEntity = useAppSelector(state => state.score.entity);
  const loading = useAppSelector(state => state.score.loading);
  const updating = useAppSelector(state => state.score.updating);
  const updateSuccess = useAppSelector(state => state.score.updateSuccess);
  const statutValues = Object.keys(Statut);

  const handleClose = () => {
    navigate(`/score${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEvaluations({}));
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
    if (values.valeur !== undefined && typeof values.valeur !== 'number') {
      values.valeur = Number(values.valeur);
    }
    if (values.pourcentage !== undefined && typeof values.pourcentage !== 'number') {
      values.pourcentage = Number(values.pourcentage);
    }
    values.dateCalcul = convertDateTimeToServer(values.dateCalcul);

    const entity = {
      ...scoreEntity,
      ...values,
      evaluation: evaluations.find(it => it.id.toString() === values.evaluation?.toString()),
      competence: competences.find(it => it.id.toString() === values.competence?.toString()),
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
          dateCalcul: displayDefaultDateTime(),
        }
      : {
          statut: 'CONFORME',
          ...scoreEntity,
          dateCalcul: convertDateTimeFromServer(scoreEntity.dateCalcul),
          evaluation: scoreEntity?.evaluation?.id,
          competence: scoreEntity?.competence?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.score.home.createOrEditLabel" data-cy="ScoreCreateUpdateHeading">
            <Translate contentKey="skillTestApp.score.home.createOrEditLabel">Create or edit a Score</Translate>
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
                  id="score-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.score.valeur')}
                id="score-valeur"
                name="valeur"
                data-cy="valeur"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.score.pourcentage')}
                id="score-pourcentage"
                name="pourcentage"
                data-cy="pourcentage"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('skillTestApp.score.statut')} id="score-statut" name="statut" data-cy="statut" type="select">
                {statutValues.map(statut => (
                  <option value={statut} key={statut}>
                    {translate(`skillTestApp.Statut.${statut}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('skillTestApp.score.dateCalcul')}
                id="score-dateCalcul"
                name="dateCalcul"
                data-cy="dateCalcul"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="score-evaluation"
                name="evaluation"
                data-cy="evaluation"
                label={translate('skillTestApp.score.evaluation')}
                type="select"
              >
                <option value="" key="0" />
                {evaluations
                  ? evaluations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="score-competence"
                name="competence"
                data-cy="competence"
                label={translate('skillTestApp.score.competence')}
                type="select"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/score" replace variant="info">
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

export default ScoreUpdate;
