import './dashboard.scss';

import React, { useEffect, useState } from 'react';
import { getDashboardStats } from '../discret-evaluation/discret-evaluation.api';
import { Alert, Button, Card, Col, ProgressBar, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import { useAppSelector } from 'app/config/store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faUsers,
  faClipboardCheck,
  faUserSecret,
  faCheckCircle,
  faExclamationTriangle,
  faTimesCircle,
  faListAlt,
  faQuestionCircle,
  faBriefcase,
  faRocket,
  faTrophy,
  faArrowRight,
} from '@fortawesome/free-solid-svg-icons';

interface DashboardData {
  conformitePourcentage: number;
  totalEmployees: number;
  totalEvaluations: number;
  evaluationsConformes: number;
  evaluationsAAmeliorer: number;
  evaluationsNonConformes: number;
}

const Dashboard = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const [stats, setStats] = useState<DashboardData | null>(null);
  const [statsError, setStatsError] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      getDashboardStats()
        .then(res => setStats(res.data))
        .catch(() => setStatsError(true));
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return (
      <div className="dashboard-landing">
        <FontAwesomeIcon icon={faRocket} size="3x" className="mb-3" style={{ color: '#4f46e5' }} />
        <h1 className="landing-title">SkillTestApp</h1>
        <p className="landing-subtitle">Plateforme intelligente de v&eacute;rification et suivi des comp&eacute;tences RH</p>
        <div className="landing-cta">
          <Button as={Link as any} to="/login" variant="primary" size="lg">
            Se connecter <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
          </Button>
        </div>
      </div>
    );
  }

  const barVariant = stats
    ? stats.conformitePourcentage >= 80
      ? 'success'
      : stats.conformitePourcentage >= 50
        ? 'warning'
        : 'danger'
    : 'secondary';

  const conformityColor = stats
    ? stats.conformitePourcentage >= 80
      ? '#10b981'
      : stats.conformitePourcentage >= 50
        ? '#f59e0b'
        : '#ef4444'
    : '#94a3b8';

  return (
    <div>
      {/* Header */}
      <div className="dashboard-header">
        <h2 className="dashboard-greeting">
          Bonjour, <span style={{ color: '#4f46e5' }}>{account?.login || 'utilisateur'}</span>
        </h2>
        <p className="dashboard-subtitle">Voici un aper&ccedil;u de vos comp&eacute;tences et &eacute;valuations</p>
      </div>

      {/* KPI Cards */}
      {stats && (
        <>
          <Row className="mb-4 g-3">
            <Col lg={3} sm={6}>
              <Card className="kpi-card h-100">
                <Card.Body>
                  <div className="kpi-icon-wrapper" style={{ background: 'rgba(79, 70, 229, 0.1)' }}>
                    <FontAwesomeIcon icon={faUsers} style={{ color: '#4f46e5' }} />
                  </div>
                  <div className="kpi-value" style={{ color: '#4f46e5' }}>
                    {stats.totalEmployees}
                  </div>
                  <div className="kpi-label">Employ&eacute;s</div>
                </Card.Body>
              </Card>
            </Col>
            <Col lg={3} sm={6}>
              <Card className="kpi-card h-100">
                <Card.Body>
                  <div className="kpi-icon-wrapper" style={{ background: 'rgba(6, 182, 212, 0.1)' }}>
                    <FontAwesomeIcon icon={faClipboardCheck} style={{ color: '#06b6d4' }} />
                  </div>
                  <div className="kpi-value" style={{ color: '#06b6d4' }}>
                    {stats.totalEvaluations}
                  </div>
                  <div className="kpi-label">&Eacute;valuations</div>
                </Card.Body>
              </Card>
            </Col>
            <Col lg={3} sm={6}>
              <Card className="kpi-card h-100">
                <Card.Body>
                  <div className="kpi-icon-wrapper" style={{ background: 'rgba(16, 185, 129, 0.1)' }}>
                    <FontAwesomeIcon icon={faCheckCircle} style={{ color: '#10b981' }} />
                  </div>
                  <div className="kpi-value" style={{ color: '#10b981' }}>
                    {stats.evaluationsConformes}
                  </div>
                  <div className="kpi-label">Conformes</div>
                </Card.Body>
              </Card>
            </Col>
            <Col lg={3} sm={6}>
              <Card className="kpi-card h-100">
                <Card.Body>
                  <div className="kpi-icon-wrapper" style={{ background: 'rgba(239, 68, 68, 0.1)' }}>
                    <FontAwesomeIcon icon={faTimesCircle} style={{ color: '#ef4444' }} />
                  </div>
                  <div className="kpi-value" style={{ color: '#ef4444' }}>
                    {stats.evaluationsNonConformes}
                  </div>
                  <div className="kpi-label">Non conformes</div>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          {/* Conformity bar */}
          <Row className="mb-4 g-3">
            <Col lg={8}>
              <Card className="conformity-card h-100">
                <Card.Body>
                  <div className="conformity-title">Taux de conformit&eacute; global</div>
                  <div className="d-flex align-items-end mb-3">
                    <span className="conformity-value" style={{ color: conformityColor }}>
                      {stats.conformitePourcentage.toFixed(1)}%
                    </span>
                  </div>
                  <ProgressBar now={stats.conformitePourcentage} variant={barVariant} />
                </Card.Body>
              </Card>
            </Col>
            <Col lg={4}>
              <Card className="kpi-card h-100">
                <Card.Body>
                  <div className="kpi-icon-wrapper" style={{ background: 'rgba(245, 158, 11, 0.1)' }}>
                    <FontAwesomeIcon icon={faExclamationTriangle} style={{ color: '#f59e0b' }} />
                  </div>
                  <div className="kpi-value" style={{ color: '#f59e0b' }}>
                    {stats.evaluationsAAmeliorer}
                  </div>
                  <div className="kpi-label">&Agrave; am&eacute;liorer</div>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </>
      )}

      {statsError && (
        <Alert variant="warning" className="mb-4" style={{ borderRadius: '1rem', border: 'none' }}>
          Impossible de charger les statistiques. V&eacute;rifiez vos permissions.
        </Alert>
      )}

      {/* Quick Access */}
      <div className="quick-access-title">
        <FontAwesomeIcon icon={faRocket} />
        Acc&egrave;s rapide
      </div>
      <Row className="g-3">
        <Col lg={4} md={6}>
          <Card as={Link} to="/discret-evaluation" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #312e81, #4f46e5)' }}>
                <FontAwesomeIcon icon={faUserSecret} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Mode Discret</Card.Title>
              <Card.Text>&Eacute;valuer un employ&eacute; de mani&egrave;re invisible</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/employee" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #1e40af, #3b82f6)' }}>
                <FontAwesomeIcon icon={faUsers} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Employ&eacute;s</Card.Title>
              <Card.Text>G&eacute;rer la liste des employ&eacute;s</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/evaluation" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #0e7490, #06b6d4)' }}>
                <FontAwesomeIcon icon={faClipboardCheck} style={{ color: '#fff' }} />
              </div>
              <Card.Title>&Eacute;valuations</Card.Title>
              <Card.Text>Consulter toutes les &eacute;valuations</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/test" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #b45309, #f59e0b)' }}>
                <FontAwesomeIcon icon={faListAlt} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Tests</Card.Title>
              <Card.Text>Cr&eacute;er et g&eacute;rer les tests</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/competence" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #047857, #10b981)' }}>
                <FontAwesomeIcon icon={faTrophy} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Comp&eacute;tences</Card.Title>
              <Card.Text>R&eacute;f&eacute;rentiel des comp&eacute;tences</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/question" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #b91c1c, #ef4444)' }}>
                <FontAwesomeIcon icon={faQuestionCircle} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Questions</Card.Title>
              <Card.Text>Banque de questions</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/poste" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #334155, #64748b)' }}>
                <FontAwesomeIcon icon={faBriefcase} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Postes</Card.Title>
              <Card.Text>G&eacute;rer les postes</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/score" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #92400e, #d97706)' }}>
                <FontAwesomeIcon icon={faTrophy} style={{ color: '#fff' }} />
              </div>
              <Card.Title>Scores</Card.Title>
              <Card.Text>Scores d&eacute;taill&eacute;s</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4} md={6}>
          <Card as={Link} to="/reponse" className="quick-card h-100">
            <Card.Body className="text-center">
              <div className="quick-icon" style={{ background: 'linear-gradient(135deg, #475569, #94a3b8)' }}>
                <FontAwesomeIcon icon={faClipboardCheck} style={{ color: '#fff' }} />
              </div>
              <Card.Title>R&eacute;ponses</Card.Title>
              <Card.Text>Historique des r&eacute;ponses</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
