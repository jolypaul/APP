import './discret-evaluation.scss';

import React, { useState, useEffect } from 'react';
import { Alert, Badge, Button, Card, Form, ProgressBar } from 'react-bootstrap';
import { startDiscretEvaluation, submitAnswerManager, finalizeEvaluation } from './discret-evaluation.api';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUserSecret, faArrowRight, faCheck, faRedo, faTrophy } from '@fortawesome/free-solid-svg-icons';

const DiscretEvaluation = () => {
  // State: wizard step
  const [step, setStep] = useState<'start' | 'questions' | 'done'>('start');

  // Start form
  const [employeeId, setEmployeeId] = useState('');
  const [testId, setTestId] = useState('');
  const [managerId, setManagerId] = useState('');
  const [evaluationId, setEvaluationId] = useState<number | null>(null);

  // Questions
  const [questions, setQuestions] = useState<any[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  // Answer form
  const [contenu, setContenu] = useState('');
  const [estCorrecte, setEstCorrecte] = useState(false);
  const [commentaire, setCommentaire] = useState('');

  // Feedback
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [result, setResult] = useState<any>(null);

  // Employee and Test lists
  const [employees, setEmployees] = useState<any[]>([]);
  const [tests, setTests] = useState<any[]>([]);

  useEffect(() => {
    axios.get('/api/employees?size=1000').then(res => setEmployees(res.data));
    axios.get('/api/tests?size=1000').then(res => setTests(res.data));
  }, []);

  const handleStart = async () => {
    setError('');
    try {
      const res = await startDiscretEvaluation({
        employeeId: Number(employeeId),
        testId: Number(testId),
        managerId: Number(managerId),
      });
      setEvaluationId(res.data.id);
      // Load questions for the test
      const qRes = await axios.get(`/api/questions?testId.equals=${testId}&size=1000`);
      setQuestions(qRes.data);
      setCurrentIndex(0);
      setStep('questions');
      setMessage('Évaluation discrète démarrée.');
    } catch (e: any) {
      setError(e.response?.data?.detail || 'Erreur lors du démarrage');
    }
  };

  const handleSubmitAnswer = async () => {
    setError('');
    if (!evaluationId || !questions[currentIndex]) return;
    try {
      await submitAnswerManager({
        evaluationId,
        questionId: questions[currentIndex].id,
        contenu,
        estCorrecte,
        commentaireManager: commentaire,
      });
      setContenu('');
      setEstCorrecte(false);
      setCommentaire('');
      if (currentIndex < questions.length - 1) {
        setCurrentIndex(currentIndex + 1);
        setMessage(`Réponse ${currentIndex + 1}/${questions.length} enregistrée.`);
      } else {
        // Finalize
        const res = await finalizeEvaluation(evaluationId);
        setResult(res.data);
        setStep('done');
        setMessage('Évaluation finalisée.');
      }
    } catch (e: any) {
      setError(e.response?.data?.detail || 'Erreur lors de la soumission');
    }
  };

  if (step === 'start') {
    return (
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
                <Form.Select value={employeeId} onChange={e => setEmployeeId(e.target.value)}>
                  <option value="">S&eacute;lectionner un employ&eacute;...</option>
                  {employees.map((emp: any) => (
                    <option key={emp.id} value={emp.id}>
                      {emp.prenom} {emp.nom} ({emp.matricule})
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>Test de comp&eacute;tences</Form.Label>
                <Form.Select value={testId} onChange={e => setTestId(e.target.value)}>
                  <option value="">S&eacute;lectionner un test...</option>
                  {tests.map((t: any) => (
                    <option key={t.id} value={t.id}>
                      {t.titre}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>Manager (&eacute;valuateur)</Form.Label>
                <Form.Select value={managerId} onChange={e => setManagerId(e.target.value)}>
                  <option value="">S&eacute;lectionner le manager...</option>
                  {employees.map((emp: any) => (
                    <option key={emp.id} value={emp.id}>
                      {emp.prenom} {emp.nom}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Button variant="primary" onClick={handleStart} disabled={!employeeId || !testId || !managerId}>
                D&eacute;marrer l&apos;&eacute;valuation <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
              </Button>
            </Form>
          </Card.Body>
        </Card>
      </div>
    );
  }

  if (step === 'questions') {
    const currentQuestion = questions[currentIndex];
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
              <Form.Group className="mb-3">
                <Form.Label style={{ fontWeight: 600, color: '#334155' }}>R&eacute;ponse de l&apos;employ&eacute;</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={contenu}
                  onChange={e => setContenu(e.target.value)}
                  placeholder="Saisir la r&eacute;ponse observ&eacute;e..."
                  style={{ borderRadius: '0.75rem', border: '2px solid #e2e8f0' }}
                />
              </Form.Group>
              <Form.Check
                type="switch"
                id="correcte-switch"
                label="R&eacute;ponse correcte"
                checked={estCorrecte}
                onChange={e => setEstCorrecte(e.target.checked)}
                className="mb-3"
                style={{ fontSize: '1rem' }}
              />
              <Form.Group className="mb-4">
                <Form.Label style={{ fontWeight: 600, color: '#334155' }}>Commentaire (optionnel)</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={commentaire}
                  onChange={e => setCommentaire(e.target.value)}
                  placeholder="Observations du manager..."
                  style={{ borderRadius: '0.75rem', border: '2px solid #e2e8f0' }}
                />
              </Form.Group>
              <div className="d-flex justify-content-end">
                <Button
                  variant="primary"
                  onClick={handleSubmitAnswer}
                  disabled={!contenu}
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
  }

  // Step: done
  const statusColor = result?.statut === 'CONFORME' ? '#10b981' : result?.statut === 'A_AMELIORER' ? '#f59e0b' : '#ef4444';
  const statusBg =
    result?.statut === 'CONFORME'
      ? 'rgba(16,185,129,0.1)'
      : result?.statut === 'A_AMELIORER'
        ? 'rgba(245,158,11,0.1)'
        : 'rgba(239,68,68,0.1)';

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
            <FontAwesomeIcon icon={faTrophy} size="3x" style={{ color: statusColor }} />
            <div className="result-score" style={{ color: statusColor }}>
              {result.scoreTotal?.toFixed(1)}%
            </div>
            <div className="result-status" style={{ color: statusColor, background: statusBg }}>
              {result.statut}
            </div>
            <p className="text-muted mt-3 mb-4">Mode : {result.mode}</p>
            <Button
              variant="primary"
              onClick={() => {
                setStep('start');
                setResult(null);
                setMessage('');
              }}
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

export default DiscretEvaluation;
