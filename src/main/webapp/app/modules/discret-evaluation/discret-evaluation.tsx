import './discret-evaluation.scss';

import React, { useState, useEffect } from 'react';
import { Alert, Badge, Button, Card, Form, ProgressBar } from 'react-bootstrap';
import {
  finalizeEvaluation,
  IAnswerCorrectionResult,
  IDiscretQuestion,
  startDiscretEvaluation,
  submitAnswerManager,
} from './discret-evaluation.api';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUserSecret, faArrowRight, faCheck, faRedo, faTrophy } from '@fortawesome/free-solid-svg-icons';
import { useAppSelector } from 'app/config/store';
import { IEmployee } from 'app/shared/model/employee.model';
import { ITest } from 'app/shared/model/test.model';

type EvaluationStep = 'start' | 'questions' | 'done';

interface IStartStepProps {
  employeeId: string;
  testId: string;
  employees: IEmployee[];
  tests: ITest[];
  managerDisplayName?: string;
  error: string;
  startLoading: boolean;
  onEmployeeChange: (value: string) => void;
  onTestChange: (value: string) => void;
  onStart: () => void;
}

interface IQuestionsStepProps {
  currentIndex: number;
  questions: IDiscretQuestion[];
  currentQuestion?: IDiscretQuestion;
  contenu: string;
  commentaire: string;
  message: string;
  error: string;
  submitting: boolean;
  lastCorrection: IAnswerCorrectionResult | null;
  onContenuChange: (value: string) => void;
  onCommentaireChange: (value: string) => void;
  onSubmit: () => void;
}

interface IDoneStepProps {
  message: string;
  result: any;
  onRestart: () => void;
}

const renderQuestionSupport = (
  currentQuestion: IDiscretQuestion | undefined,
  contenu: string,
  onContenuChange: (value: string) => void,
) => {
  if (!currentQuestion) {
    return null;
  }

  if (currentQuestion.type === 'QCM' && currentQuestion.options.length > 0) {
    return (
      <div className="mb-4">
        <Form.Label style={{ fontWeight: 600, color: '#334155' }}>Choix propos&eacute;s</Form.Label>
        <div className="d-flex flex-column gap-2">
          {currentQuestion.options.map((option, index) => (
            <Button
              key={`${currentQuestion.id}-${index}`}
              variant={contenu === option ? 'primary' : 'outline-primary'}
              className="text-start"
              onClick={() => onContenuChange(option)}
            >
              {String.fromCharCode(65 + index)}. {option}
            </Button>
          ))}
        </div>
      </div>
    );
  }

  if (currentQuestion.type === 'VRAI_FAUX') {
    return (
      <div className="mb-4 d-flex gap-2">
        <Button variant={contenu === 'Vrai' ? 'primary' : 'outline-primary'} onClick={() => onContenuChange('Vrai')}>
          Vrai
        </Button>
        <Button variant={contenu === 'Faux' ? 'primary' : 'outline-primary'} onClick={() => onContenuChange('Faux')}>
          Faux
        </Button>
      </div>
    );
  }

  return null;
};

const StartStep = ({
  employeeId,
  testId,
  employees,
  tests,
  managerDisplayName,
  error,
  startLoading,
  onEmployeeChange,
  onTestChange,
  onStart,
}: IStartStepProps) => (
  <div className="discret-container">
    <div className="discret-header">
      <h2 className="discret-title">
        <FontAwesomeIcon icon={faUserSecret} style={{ color: '#4f46e5' }} />
        Mode Discret
      </h2>
      <p className="discret-subtitle">Le manager &eacute;value l&apos;employ&eacute; sans que celui-ci soit notifi&eacute;.</p>
    </div>
    {error && (
      <Alert variant="danger" style={{ borderRadius: '0.75rem', border: 'none' }}>
        {error}
      </Alert>
    )}
    <Card className="discret-form-card">
      <Card.Body>
        <Form>
          <Form.Group className="mb-4">
            <Form.Label>Employ&eacute; &agrave; &eacute;valuer</Form.Label>
            <Form.Select value={employeeId} onChange={e => onEmployeeChange(e.target.value)}>
              <option value="">S&eacute;lectionner un employ&eacute;...</option>
              {employees.map(emp => (
                <option key={emp.id} value={emp.id}>
                  {emp.prenom} {emp.nom} ({emp.matricule})
                </option>
              ))}
            </Form.Select>
          </Form.Group>
          <Form.Group className="mb-4">
            <Form.Label>Test de comp&eacute;tences</Form.Label>
            <Form.Select value={testId} onChange={e => onTestChange(e.target.value)}>
              <option value="">S&eacute;lectionner un test...</option>
              {tests.map(test => (
                <option key={test.id} value={test.id}>
                  {test.titre}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
          <Form.Group className="mb-4">
            <Form.Label>Manager (&eacute;valuateur)</Form.Label>
            <Form.Control value={managerDisplayName ?? ''} readOnly />
          </Form.Group>
          <Button variant="primary" onClick={onStart} disabled={!employeeId || !testId || startLoading}>
            D&eacute;marrer l&apos;&eacute;valuation <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
          </Button>
        </Form>
      </Card.Body>
    </Card>
  </div>
);

const QuestionsStep = ({
  currentIndex,
  questions,
  currentQuestion,
  contenu,
  commentaire,
  message,
  error,
  submitting,
  lastCorrection,
  onContenuChange,
  onCommentaireChange,
  onSubmit,
}: IQuestionsStepProps) => {
  const progressPct = ((currentIndex + 1) / questions.length) * 100;

  return (
    <div className="discret-container">
      <div className="discret-header">
        <h2 className="discret-title">
          <FontAwesomeIcon icon={faUserSecret} style={{ color: '#4f46e5' }} />
          &Eacute;valuation en cours
        </h2>
      </div>
      <div className="question-progress">
        <span className="progress-info">
          {currentIndex + 1} / {questions.length}
        </span>
        <ProgressBar now={progressPct} />
      </div>
      <Alert variant="secondary" style={{ borderRadius: '0.75rem', border: 'none' }}>
        L&apos;&eacute;valuateur pose la question &agrave; l&apos;employ&eacute;, puis saisit la r&eacute;ponse. La correction est faite
        automatiquement.
      </Alert>
      {message && (
        <Alert variant="info" style={{ borderRadius: '0.75rem', border: 'none' }}>
          {message}
        </Alert>
      )}
      {error && (
        <Alert variant="danger" style={{ borderRadius: '0.75rem', border: 'none' }}>
          {error}
        </Alert>
      )}
      <Card className="question-card">
        <Card.Body>
          <div className="question-badges">
            <Badge bg="primary" pill>
              {currentQuestion?.type}
            </Badge>
            <Badge bg="info" pill>
              {currentQuestion?.niveau}
            </Badge>
            {currentQuestion?.points && (
              <Badge bg="warning" text="dark" pill>
                {currentQuestion.points} pts
              </Badge>
            )}
          </div>
          <div className="question-enonce">{currentQuestion?.enonce}</div>
          <Form>
            {renderQuestionSupport(currentQuestion, contenu, onContenuChange)}
            <Form.Group className="mb-3">
              <Form.Label style={{ fontWeight: 600, color: '#334155' }}>R&eacute;ponse de l&apos;employ&eacute;</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={contenu}
                onChange={e => onContenuChange(e.target.value)}
                placeholder="Saisir la r&eacute;ponse observ&eacute;e..."
                style={{ borderRadius: '0.75rem', border: '2px solid #e2e8f0' }}
              />
            </Form.Group>
            <Form.Group className="mb-4">
              <Form.Label style={{ fontWeight: 600, color: '#334155' }}>Commentaire (optionnel)</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={commentaire}
                onChange={e => onCommentaireChange(e.target.value)}
                placeholder="Observations du manager..."
                style={{ borderRadius: '0.75rem', border: '2px solid #e2e8f0' }}
              />
            </Form.Group>
            {lastCorrection && lastCorrection.questionId === currentQuestion?.id && (
              <Alert variant={lastCorrection.estCorrecte ? 'success' : 'warning'} style={{ borderRadius: '0.75rem', border: 'none' }}>
                <strong>{lastCorrection.estCorrecte ? 'Correction conforme.' : 'Correction &agrave; revoir.'}</strong>{' '}
                {lastCorrection.commentaireIa}
              </Alert>
            )}
            <div className="d-flex justify-content-end">
              <Button
                variant="primary"
                onClick={onSubmit}
                disabled={!contenu.trim() || submitting}
                style={{
                  background: 'linear-gradient(135deg, #4f46e5, #6366f1)',
                  border: 'none',
                  borderRadius: '0.75rem',
                  padding: '0.7rem 2rem',
                  fontWeight: 600,
                }}
              >
                {currentIndex < questions.length - 1 ? (
                  <>
                    Suivante <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
                  </>
                ) : (
                  <>
                    Finaliser <FontAwesomeIcon icon={faCheck} className="ms-2" />
                  </>
                )}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

const DoneStep = ({ message, result, onRestart }: IDoneStepProps) => {
  const statusVariant = result?.statut === 'CONFORME' ? 'success' : result?.statut === 'A_AMELIORER' ? 'warning' : 'danger';

  return (
    <div className="discret-container">
      <div className="discret-header">
        <h2 className="discret-title">
          <FontAwesomeIcon icon={faTrophy} style={{ color: '#f59e0b' }} />
          &Eacute;valuation termin&eacute;e
        </h2>
      </div>
      {message && (
        <Alert variant="success" style={{ borderRadius: '0.75rem', border: 'none' }}>
          {message}
        </Alert>
      )}
      {result && (
        <Card className="result-card">
          <Card.Body>
            <FontAwesomeIcon icon={faTrophy} size="3x" className={`result-accent result-accent--${statusVariant}`} />
            <div className={`result-score result-score--${statusVariant}`}>{result.scoreTotal?.toFixed(1)}%</div>
            <div className={`result-status result-status--${statusVariant}`}>{result.statut}</div>
            <p className="text-muted mt-3 mb-4">Mode : {result.mode}</p>
            <Button
              variant="primary"
              onClick={onRestart}
              style={{
                background: 'linear-gradient(135deg, #4f46e5, #6366f1)',
                border: 'none',
                borderRadius: '50px',
                padding: '0.7rem 2rem',
                fontWeight: 600,
              }}
            >
              <FontAwesomeIcon icon={faRedo} className="me-2" />
              Nouvelle &eacute;valuation
            </Button>
          </Card.Body>
        </Card>
      )}
    </div>
  );
};

const DiscretEvaluation = () => {
  const account = useAppSelector(state => state.authentication.account);

  // State: wizard step
  const [step, setStep] = useState<EvaluationStep>('start');

  // Start form
  const [employeeId, setEmployeeId] = useState('');
  const [testId, setTestId] = useState('');
  const [evaluationId, setEvaluationId] = useState<number | null>(null);
  const [manager, setManager] = useState<IEmployee | null>(null);
  const [startLoading, setStartLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Questions
  const [questions, setQuestions] = useState<IDiscretQuestion[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  // Answer form
  const [contenu, setContenu] = useState('');
  const [commentaire, setCommentaire] = useState('');
  const [lastCorrection, setLastCorrection] = useState<IAnswerCorrectionResult | null>(null);

  // Feedback
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [result, setResult] = useState<any>(null);

  // Employee and Test lists
  const [employees, setEmployees] = useState<IEmployee[]>([]);
  const [tests, setTests] = useState<ITest[]>([]);

  useEffect(() => {
    axios.get('/api/employees?size=1000').then(res => setEmployees(res.data));
    axios.get('/api/tests?size=1000').then(res => setTests(res.data.filter((test: ITest) => test.mode === 'DISCRET')));
  }, []);

  useEffect(() => {
    if (!employees.length) {
      return;
    }

    const authenticatedManager = employees.find(emp => emp.email?.toLowerCase() === account?.email?.toLowerCase()) ?? null;
    setManager(authenticatedManager);
  }, [account?.email, employees]);

  const resetQuestionState = () => {
    setContenu('');
    setCommentaire('');
    setLastCorrection(null);
  };

  const managerDisplayName = manager
    ? `${manager.prenom ?? ''} ${manager.nom ?? ''}`.trim()
    : account?.firstName || account?.lastName
      ? `${account?.firstName ?? ''} ${account?.lastName ?? ''}`.trim()
      : account?.login;

  const handleStart = async () => {
    setError('');
    setStartLoading(true);
    try {
      // Use the matched employee profile, or pick the first employee as fallback for admin accounts
      const resolvedManagerId = manager?.id ?? employees[0]?.id;
      const res = await startDiscretEvaluation({
        employeeId: Number(employeeId),
        testId: Number(testId),
        managerId: resolvedManagerId ? Number(resolvedManagerId) : undefined,
      });
      setEvaluationId(res.data.evaluation.id);
      setQuestions(res.data.questions);
      setCurrentIndex(0);
      resetQuestionState();
      setStep('questions');
      setMessage('Évaluation discrète démarrée avec un questionnaire de 7 questions.');
    } catch (e: any) {
      const msg =
        e.response?.data?.detail || e.response?.data?.message || e.response?.data?.title || "Erreur lors du démarrage de l'évaluation";
      setError(msg);
    } finally {
      setStartLoading(false);
    }
  };

  const handleSubmitAnswer = async () => {
    setError('');
    if (!evaluationId || !questions[currentIndex]) return;
    setSubmitting(true);
    try {
      const correction = await submitAnswerManager({
        evaluationId,
        questionId: questions[currentIndex].id,
        contenu,
        commentaireManager: commentaire,
      });
      setLastCorrection(correction.data);
      if (currentIndex < questions.length - 1) {
        resetQuestionState();
        setCurrentIndex(currentIndex + 1);
        setMessage(
          `Réponse ${currentIndex + 1}/${questions.length} enregistrée. Correction automatique: ${
            correction.data.estCorrecte ? 'conforme' : 'à revoir'
          }.`,
        );
      } else {
        // Finalize
        const res = await finalizeEvaluation(evaluationId);
        setResult(res.data);
        setStep('done');
        setMessage('Évaluation finalisée.');
      }
    } catch (e: any) {
      setError(e.response?.data?.detail || 'Erreur lors de la soumission');
    } finally {
      setSubmitting(false);
    }
  };

  const currentQuestion = questions[currentIndex];

  if (step === 'start') {
    return (
      <StartStep
        employeeId={employeeId}
        testId={testId}
        employees={employees}
        tests={tests}
        managerDisplayName={managerDisplayName}
        error={error}
        startLoading={startLoading}
        onEmployeeChange={setEmployeeId}
        onTestChange={setTestId}
        onStart={handleStart}
      />
    );
  }

  if (step === 'questions') {
    return (
      <QuestionsStep
        currentIndex={currentIndex}
        questions={questions}
        currentQuestion={currentQuestion}
        contenu={contenu}
        commentaire={commentaire}
        message={message}
        error={error}
        submitting={submitting}
        lastCorrection={lastCorrection}
        onContenuChange={setContenu}
        onCommentaireChange={setCommentaire}
        onSubmit={handleSubmitAnswer}
      />
    );
  }

  return (
    <DoneStep
      message={message}
      result={result}
      onRestart={() => {
        setStep('start');
        setResult(null);
        setMessage('');
      }}
    />
  );
};

export default DiscretEvaluation;
