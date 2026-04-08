import React, { useEffect } from 'react';
import { Alert, Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isEmail, translate } from 'react-jhipster';
import { Link } from 'react-router';

import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handlePasswordResetInit, reset } from '../password-reset.reducer';

export const PasswordResetInit = () => {
  const dispatch = useAppDispatch();

  useEffect(
    () => () => {
      dispatch(reset());
    },
    [],
  );

  const handleValidSubmit = ({ email }) => {
    dispatch(handlePasswordResetInit(email));
  };

  const successMessage = useAppSelector(state => state.passwordReset.successMessage);
  const resetLink = useAppSelector(state => state.passwordReset.resetLink);
  const emailSent = useAppSelector(state => state.passwordReset.emailSent);

  useEffect(() => {
    if (successMessage) {
      toast.success(translate(successMessage));
    }
  }, [successMessage]);

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h1>
            <Translate contentKey="reset.request.title">Reset your password</Translate>
          </h1>
          <Alert variant="warning">
            <p>
              <Translate contentKey="reset.request.messages.info">Enter the email address you used to register</Translate>
            </p>
            <p className="mb-0 text-muted">
              Si aucun SMTP n&apos;est configur&eacute; en d&eacute;veloppement, un lien de r&eacute;initialisation local sera
              affich&eacute; ici.
            </p>
          </Alert>
          {successMessage && !emailSent && resetLink && (
            <Alert variant="info">
              <p className="mb-2">Aucun email r&eacute;el n&apos;a pu &ecirc;tre envoy&eacute; depuis l&apos;environnement courant.</p>
              <Button as={Link as any} to={resetLink.replace('http://127.0.0.1:8080', '')} variant="outline-primary">
                Ouvrir le lien de r&eacute;initialisation
              </Button>
            </Alert>
          )}
          <ValidatedForm onSubmit={handleValidSubmit}>
            <ValidatedField
              name="email"
              label={translate('global.form.email.label')}
              placeholder={translate('global.form.email.placeholder')}
              type="email"
              validate={{
                required: { value: true, message: translate('global.messages.validate.email.required') },
                minLength: { value: 5, message: translate('global.messages.validate.email.minlength') },
                maxLength: { value: 254, message: translate('global.messages.validate.email.maxlength') },
                validate: v => isEmail(v) || translate('global.messages.validate.email.invalid'),
              }}
              data-cy="emailResetPassword"
            />
            <Button variant="primary" type="submit" data-cy="submit">
              <Translate contentKey="reset.request.form.button">Reset password</Translate>
            </Button>
          </ValidatedForm>
        </Col>
      </Row>
    </div>
  );
};

export default PasswordResetInit;
