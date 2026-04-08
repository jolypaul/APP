import React, { useEffect } from 'react';
import { Alert, Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';
import { toast } from 'react-toastify';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getPostes } from 'app/entities/poste/poste.reducer';
import { Role } from 'app/shared/model/enumerations/role.model';

import { createEntity, getEntity, reset, updateEntity } from './employee.reducer';

export const EmployeeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const postes = useAppSelector(state => state.poste.entities);
  const employeeEntity = useAppSelector(state => state.employee.entity);
  const loading = useAppSelector(state => state.employee.loading);
  const updating = useAppSelector(state => state.employee.updating);
  const updateSuccess = useAppSelector(state => state.employee.updateSuccess);
  const roleValues = Object.keys(Role);

  const handleClose = () => {
    navigate(`/employee${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPostes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      if (isNew) {
        if (employeeEntity.canLogin) {
          toast.success(
            `Compte créé pour ${employeeEntity.email}. Mot de passe initial: ${employeeEntity.defaultPasswordHint ?? 'motdepasse'}. L'utilisateur peut le changer via Compte > Mot de passe.`,
          );
        } else {
          toast.info(`Le profil ${employeeEntity.email} a été créé sans accès de connexion car son rôle est EMPLOYEE.`);
        }
      }
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...employeeEntity,
      ...values,
      poste: postes.find(it => it.id.toString() === values.poste?.toString()),
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
          role: 'ADMIN',
          ...employeeEntity,
          poste: employeeEntity?.poste?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="skillTestApp.employee.home.createOrEditLabel" data-cy="EmployeeCreateUpdateHeading">
            <Translate contentKey="skillTestApp.employee.home.createOrEditLabel">Create or edit a Employee</Translate>
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
                  id="employee-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('skillTestApp.employee.matricule')}
                id="employee-matricule"
                name="matricule"
                data-cy="matricule"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.employee.nom')}
                id="employee-nom"
                name="nom"
                data-cy="nom"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.employee.prenom')}
                id="employee-prenom"
                name="prenom"
                data-cy="prenom"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.employee.email')}
                id="employee-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('skillTestApp.employee.telephone')}
                id="employee-telephone"
                name="telephone"
                data-cy="telephone"
                type="text"
              />
              <ValidatedField
                label={translate('skillTestApp.employee.dateEmbauche')}
                id="employee-dateEmbauche"
                name="dateEmbauche"
                data-cy="dateEmbauche"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('skillTestApp.employee.role')} id="employee-role" name="role" data-cy="role" type="select">
                {roleValues.map(role => (
                  <option value={role} key={role}>
                    {translate(`skillTestApp.Role.${role}`)}
                  </option>
                ))}
              </ValidatedField>
              <Alert variant="secondary">
                Les profils `ADMIN`, `RH`, `MANAGER` et `EXPERT` re&ccedil;oivent automatiquement un compte de connexion avec
                l&apos;identifiant &eacute;gal &agrave; l&apos;email et le mot de passe initial `motdepasse`.
                <br />
                Le profil `EMPLOYEE` est cr&eacute;&eacute; sans acc&egrave;s de connexion.
                <br />
                Un utilisateur connect&eacute; peut changer son mot de passe via le menu Compte &gt; Mot de passe.
              </Alert>
              <ValidatedField
                id="employee-poste"
                name="poste"
                data-cy="poste"
                label={translate('skillTestApp.employee.poste')}
                type="select"
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/employee" replace variant="info">
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

export default EmployeeUpdate;
