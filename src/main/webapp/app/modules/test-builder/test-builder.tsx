import './test-builder.scss';

import React, { useEffect, useState } from 'react';
import { Accordion, Alert, Badge, Button, Card, Col, Form, Row, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft, faCheckCircle, faCopy, faGripVertical, faPlus, faSave, faTrash } from '@fortawesome/free-solid-svg-icons';

interface QuestionItem {
  _key: string;
  id?: number;
  enonce: string;
  type: 'QCM' | 'OUVERTE' | 'VRAI_FAUX' | 'PRATIQUE';
  niveau: 'DEBUTANT' | 'INTERMEDIAIRE' | 'AVANCE' | 'EXPERT';
  points: number;
  choixMultiple: string;
  reponseAttendue: string;
}

interface CompetenceOption {
  id: number;
  nom: string;
}

const emptyQuestion = (): QuestionItem => ({
  _key: crypto.randomUUID(),
  enonce: '',
  type: 'QCM',
  niveau: 'INTERMEDIAIRE',
  points: 1,
  choixMultiple: '',
  reponseAttendue: '',
});

const TestBuilder = () => {
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = !id;

  const [titre, setTitre] = useState('');
  const [description, setDescription] = useState('');
  const [mode, setMode] = useState<'CLASSIQUE' | 'DISCRET'>('CLASSIQUE');
  const [duree, setDuree] = useState<number | ''>('');
  const [actif, setActif] = useState(true);
  const [competenceIds, setCompetenceIds] = useState<number[]>([]);
  const [questions, setQuestions] = useState<QuestionItem[]>([emptyQuestion()]);

  const [competences, setCompetences] = useState<CompetenceOption[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState('');
  const [activeKey, setActiveKey] = useState<string | null>('0');

  useEffect(() => {
    axios.get<CompetenceOption[]>('/api/competences?size=1000').then(res => {
      setCompetences(
        (res.data || []).map((c: any) => ({
          id: c.id,
          nom: c.nom,
        })),
      );
    });

    if (!isNew) {
      setLoading(true);
      axios
        .get(`/api/test-builder/${id}`)
        .then(res => {
          const d = res.data;
          setTitre(d.titre || '');
          setDescription(d.description || '');
          setMode(d.mode || 'CLASSIQUE');
          setDuree(d.duree ?? '');
          setActif(d.actif ?? true);
          setCompetenceIds(d.competenceIds ? [...d.competenceIds] : []);
          if (d.questions && d.questions.length > 0) {
            setQuestions(
              d.questions.map((q: any) => ({
                _key: crypto.randomUUID(),
                id: q.id,
                enonce: q.enonce || '',
                type: q.type || 'QCM',
                niveau: q.niveau || 'INTERMEDIAIRE',
                points: q.points ?? 1,
                choixMultiple: q.choixMultiple || '',
                reponseAttendue: q.reponseAttendue || '',
              })),
            );
          }
        })
        .catch(() => setError('Impossible de charger le test.'))
        .finally(() => setLoading(false));
    }
  }, []);

  const updateQuestion = (index: number, field: string, value: any) => {
    setQuestions(prev => prev.map((q, i) => (i === index ? { ...q, [field]: value } : q)));
  };

  const addQuestion = () => {
    const q = emptyQuestion();
    setQuestions(prev => [...prev, q]);
    setActiveKey(String(questions.length));
  };

  const duplicateQuestion = (index: number) => {
    const copy = { ...questions[index], _key: crypto.randomUUID(), id: undefined };
    const next = [...questions];
    next.splice(index + 1, 0, copy);
    setQuestions(next);
    setActiveKey(String(index + 1));
  };

  const removeQuestion = (index: number) => {
    if (questions.length <= 1) return;
    setQuestions(prev => prev.filter((_, i) => i !== index));
    setActiveKey(null);
  };

  const totalPoints = questions.reduce((sum, q) => sum + (q.points || 0), 0);

  const handleSave = async () => {
    setError('');
    setSaved(false);

    if (!titre.trim()) {
      setError('Le titre est obligatoire.');
      return;
    }
    const invalidQ = questions.findIndex(q => !q.enonce.trim());
    if (invalidQ >= 0) {
      setError(`La question #${invalidQ + 1} n'a pas d'énoncé.`);
      setActiveKey(String(invalidQ));
      return;
    }

    setSaving(true);
    try {
      const payload = {
        id: isNew ? null : Number(id),
        titre,
        description,
        mode,
        duree: duree === '' ? null : Number(duree),
        actif,
        competenceIds,
        questions: questions.map(q => ({
          id: q.id ?? null,
          enonce: q.enonce,
          type: q.type,
          niveau: q.niveau,
          points: q.points,
          choixMultiple: q.choixMultiple || null,
          reponseAttendue: q.reponseAttendue || null,
        })),
      };
      await axios.post('/api/test-builder', payload);
      setSaved(true);
      setTimeout(() => navigate('/test'), 1200);
    } catch {
      setError('Erreur lors de la sauvegarde.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  return (
    <div className="test-builder">
      {/* Top bar */}
      <div className="builder-top-bar d-flex align-items-center justify-content-between flex-wrap gap-2 mb-4">
        <div className="d-flex align-items-center gap-3">
          <Button variant="outline-secondary" size="sm" onClick={() => navigate('/test')}>
            <FontAwesomeIcon icon={faArrowLeft} className="me-1" />
            Retour
          </Button>
          <h4 className="mb-0 builder-title">{isNew ? 'Nouveau test' : 'Modifier le test'}</h4>
        </div>
        <div className="d-flex align-items-center gap-2">
          <Badge bg="secondary" className="total-badge">
            {questions.length} question{questions.length > 1 ? 's' : ''} &middot; {totalPoints} pts
          </Badge>
          <Button variant="primary" onClick={handleSave} disabled={saving}>
            {saving ? <Spinner size="sm" animation="border" className="me-1" /> : <FontAwesomeIcon icon={faSave} className="me-1" />}
            Enregistrer
          </Button>
        </div>
      </div>

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError('')}>
          {error}
        </Alert>
      )}
      {saved && (
        <Alert variant="success">
          <FontAwesomeIcon icon={faCheckCircle} className="me-1" />
          Test enregistr&eacute; avec succ&egrave;s ! Redirection...
        </Alert>
      )}

      {/* Test metadata */}
      <Card className="builder-card mb-4">
        <Card.Body>
          <h5 className="section-title">Informations du test</h5>
          <Row className="g-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>
                  Titre <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control value={titre} onChange={e => setTitre(e.target.value)} placeholder="Ex: Test Java Senior" />
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Mode</Form.Label>
                <Form.Select value={mode} onChange={e => setMode(e.target.value as any)}>
                  <option value="CLASSIQUE">Classique</option>
                  <option value="DISCRET">Discret</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Dur&eacute;e (min)</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  value={duree}
                  onChange={e => setDuree(e.target.value === '' ? '' : Number(e.target.value))}
                  placeholder="Ex: 30"
                />
              </Form.Group>
            </Col>
            <Col md={8}>
              <Form.Group>
                <Form.Label>Description</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Description du test..."
                />
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Comp&eacute;tences</Form.Label>
                <Form.Select
                  multiple
                  value={competenceIds.map(String)}
                  onChange={e => {
                    const opts = Array.from(e.target.selectedOptions, o => Number(o.value));
                    setCompetenceIds(opts);
                  }}
                  style={{ minHeight: '68px' }}
                >
                  {competences.map(c => (
                    <option key={c.id} value={c.id}>
                      {c.nom}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={12}>
              <Form.Check type="switch" label="Actif" checked={actif} onChange={e => setActif(e.target.checked)} />
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Questions section */}
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h5 className="section-title mb-0">Questions</h5>
        <Button variant="outline-primary" size="sm" onClick={addQuestion}>
          <FontAwesomeIcon icon={faPlus} className="me-1" />
          Ajouter une question
        </Button>
      </div>

      <Accordion activeKey={activeKey ?? undefined} onSelect={(k: any) => setActiveKey(k)}>
        {questions.map((q, idx) => (
          <Accordion.Item eventKey={String(idx)} key={q._key} className="question-item mb-2">
            <Accordion.Header>
              <div className="d-flex align-items-center gap-2 w-100 pe-3">
                <FontAwesomeIcon icon={faGripVertical} className="text-muted" />
                <Badge bg={levelColor(q.niveau)} className="me-1">
                  {q.niveau}
                </Badge>
                <Badge bg={typeColor(q.type)} className="me-1">
                  {q.type}
                </Badge>
                <span className="question-label flex-grow-1">{q.enonce ? truncate(q.enonce, 60) : `Question #${idx + 1}`}</span>
                <Badge bg="dark">
                  {q.points} pt{q.points > 1 ? 's' : ''}
                </Badge>
              </div>
            </Accordion.Header>
            <Accordion.Body>
              <Row className="g-3">
                <Col md={12}>
                  <Form.Group>
                    <Form.Label>
                      &Eacute;nonc&eacute; <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={2}
                      value={q.enonce}
                      onChange={e => updateQuestion(idx, 'enonce', e.target.value)}
                      placeholder="Saisissez l'énoncé de la question..."
                    />
                  </Form.Group>
                </Col>
                <Col md={4}>
                  <Form.Group>
                    <Form.Label>Type</Form.Label>
                    <Form.Select value={q.type} onChange={e => updateQuestion(idx, 'type', e.target.value)}>
                      <option value="QCM">QCM</option>
                      <option value="OUVERTE">Ouverte</option>
                      <option value="VRAI_FAUX">Vrai / Faux</option>
                      <option value="PRATIQUE">Pratique</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={4}>
                  <Form.Group>
                    <Form.Label>Niveau</Form.Label>
                    <Form.Select value={q.niveau} onChange={e => updateQuestion(idx, 'niveau', e.target.value)}>
                      <option value="DEBUTANT">D&eacute;butant</option>
                      <option value="INTERMEDIAIRE">Interm&eacute;diaire</option>
                      <option value="AVANCE">Avanc&eacute;</option>
                      <option value="EXPERT">Expert</option>
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={4}>
                  <Form.Group>
                    <Form.Label>Points</Form.Label>
                    <Form.Control
                      type="number"
                      min={1}
                      value={q.points}
                      onChange={e => updateQuestion(idx, 'points', Number(e.target.value))}
                    />
                  </Form.Group>
                </Col>
                {(q.type === 'QCM' || q.type === 'VRAI_FAUX') && (
                  <Col md={6}>
                    <Form.Group>
                      <Form.Label>Choix (s&eacute;par&eacute;s par &laquo; ;; &raquo;)</Form.Label>
                      <Form.Control
                        as="textarea"
                        rows={2}
                        value={q.choixMultiple}
                        onChange={e => updateQuestion(idx, 'choixMultiple', e.target.value)}
                        placeholder={q.type === 'VRAI_FAUX' ? 'Vrai;; Faux' : 'Choix A;; Choix B;; Choix C'}
                      />
                    </Form.Group>
                  </Col>
                )}
                <Col md={q.type === 'QCM' || q.type === 'VRAI_FAUX' ? 6 : 12}>
                  <Form.Group>
                    <Form.Label>R&eacute;ponse attendue</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={2}
                      value={q.reponseAttendue}
                      onChange={e => updateQuestion(idx, 'reponseAttendue', e.target.value)}
                      placeholder="Réponse correcte..."
                    />
                  </Form.Group>
                </Col>
              </Row>
              <div className="d-flex justify-content-end gap-2 mt-3">
                <Button variant="outline-secondary" size="sm" onClick={() => duplicateQuestion(idx)}>
                  <FontAwesomeIcon icon={faCopy} className="me-1" />
                  Dupliquer
                </Button>
                <Button variant="outline-danger" size="sm" onClick={() => removeQuestion(idx)} disabled={questions.length <= 1}>
                  <FontAwesomeIcon icon={faTrash} className="me-1" />
                  Supprimer
                </Button>
              </div>
            </Accordion.Body>
          </Accordion.Item>
        ))}
      </Accordion>

      {/* Bottom add button */}
      <div className="text-center mt-3 mb-5">
        <Button variant="outline-primary" onClick={addQuestion}>
          <FontAwesomeIcon icon={faPlus} className="me-1" />
          Ajouter une question
        </Button>
      </div>
    </div>
  );
};

function truncate(s: string, max: number) {
  return s.length > max ? s.slice(0, max) + '...' : s;
}

function levelColor(level: string) {
  const m: Record<string, string> = { DEBUTANT: 'success', INTERMEDIAIRE: 'info', AVANCE: 'warning', EXPERT: 'danger' };
  return m[level] ?? 'secondary';
}

function typeColor(type: string) {
  const m: Record<string, string> = { QCM: 'primary', OUVERTE: 'secondary', VRAI_FAUX: 'info', PRATIQUE: 'warning' };
  return m[type] ?? 'secondary';
}

export default TestBuilder;
