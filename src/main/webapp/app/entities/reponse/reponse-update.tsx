import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getEvaluations } from 'app/entities/evaluation/evaluation.reducer';
import { getEntities as getQuestions } from 'app/entities/question/question.reducer';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './reponse.reducer';

export const ReponseUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const questions = useAppSelector(state => state.question.entities);
  const evaluations = useAppSelector(state => state.evaluation.entities);
  const reponseEntity = useAppSelector(state => state.reponse.entity);
  const loading = useAppSelector(state => state.reponse.loading);
  const updating = useAppSelector(state => state.reponse.updating);
  const updateSuccess = useAppSelector(state => state.reponse.updateSuccess);

  const handleClose = () => {
    navigate(`/reponse${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getQuestions({}));
    dispatch(getEvaluations({}));
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
    values.dateReponse = convertDateTimeToServer(values.dateReponse);

    const entity = {
      ...reponseEntity,
      ...values,
      question: questions.find(it => it.id.toString() === values.question?.toString()),
      evaluation: evaluations.find(it => it.id.toString() === values.evaluation?.toString()),
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
          dateReponse: displayDefaultDateTime(),
        }
      : {
          ...reponseEntity,
          dateReponse: convertDateTimeFromServer(reponseEntity.dateReponse),
          question: reponseEntity?.question?.id,
          evaluation: reponseEntity?.evaluation?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.reponse.home.createOrEditLabel" data-cy="ReponseCreateUpdateHeading">
            <Translate contentKey="skillTestApp.reponse.home.createOrEditLabel">Create or edit a Reponse</Translate>
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
                  id="reponse-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.reponse.contenu')}
                id="reponse-contenu"
                name="contenu"
                data-cy="contenu"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.reponse.estCorrecte')}
                id="reponse-estCorrecte"
                name="estCorrecte"
                data-cy="estCorrecte"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('skillTestApp.reponse.dateReponse')}
                id="reponse-dateReponse"
                name="dateReponse"
                data-cy="dateReponse"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.reponse.commentaireManager')}
                id="reponse-commentaireManager"
                name="commentaireManager"
                data-cy="commentaireManager"
                type="textarea"
              />
              <ValidatedField
                id="reponse-question"
                name="question"
                data-cy="question"
                label={translate('skillTestApp.reponse.question')}
                type="select"
              >
                <option value="" key="0" />
                {questions
                  ? questions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.enonce}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="reponse-evaluation"
                name="evaluation"
                data-cy="evaluation"
                label={translate('skillTestApp.reponse.evaluation')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/reponse" replace variant="info">
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

export default ReponseUpdate;
