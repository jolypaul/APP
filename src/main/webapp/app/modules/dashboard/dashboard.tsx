import './dashboard.scss';

import React, { useEffect, useState } from 'react';
import { getDashboardStats } from '../discret-evaluation/discret-evaluation.api';
import { Alert, Badge, Button, Card, Col, ProgressBar, Row } from 'react-bootstrap';
import { Link } from 'react-router';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';
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
  faCogs,
  faChartBar,
  faGraduationCap,
  faShieldAlt,
} from '@fortawesome/free-solid-svg-icons';
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core';

interface DashboardData {
  conformitePourcentage: number;
  totalEmployees: number;
  totalEvaluations: number;
  evaluationsConformes: number;
  evaluationsAAmeliorer: number;
  evaluationsNonConformes: number;
}

const ROLE_META: Record<string, { label: string; color: string; icon: IconDefinition; subtitle: string }> = {
  [Authority.ADMIN]: {
    label: 'Administrateur',
    color: '#4f46e5',
    icon: faShieldAlt,
    subtitle: 'Vue d\u2019ensemble de la plateforme',
  },
  [Authority.RH]: {
    label: 'Ressources Humaines',
    color: '#0891b2',
    icon: faUsers,
    subtitle: 'Suivi des employ\u00e9s et r\u00e9sultats',
  },
  [Authority.MANAGER]: {
    label: 'Manager',
    color: '#7c3aed',
    icon: faUserSecret,
    subtitle: 'Gestion des \u00e9valuations et tests',
  },
  [Authority.EXPERT]: {
    label: 'Expert',
    color: '#059669',
    icon: faCogs,
    subtitle: 'D\u00e9finition et \u00e9valuation des comp\u00e9tences',
  },
  [Authority.EMPLOYEE]: {
    label: 'Employ\u00e9',
    color: '#d97706',
    icon: faGraduationCap,
    subtitle: 'Votre espace de comp\u00e9tences',
  },
};

interface QuickCard {
  to: string;
  icon: IconDefinition;
  gradient: string;
  title: string;
  text: string;
  roles: string[];
}

const QUICK_CARDS: QuickCard[] = [
  {
    to: '/discret-evaluation',
    icon: faUserSecret,
    gradient: 'linear-gradient(135deg, #312e81, #4f46e5)',
    title: 'Mode Discret',
    text: '\u00c9valuer un employ\u00e9 de mani\u00e8re invisible',
    roles: [Authority.ADMIN, Authority.MANAGER],
  },
  {
    to: '/employee',
    icon: faUsers,
    gradient: 'linear-gradient(135deg, #1e40af, #3b82f6)',
    title: 'Employ\u00e9s',
    text: 'G\u00e9rer la liste des employ\u00e9s',
    roles: [Authority.ADMIN, Authority.RH],
  },
  {
    to: '/evaluation',
    icon: faClipboardCheck,
    gradient: 'linear-gradient(135deg, #0e7490, #06b6d4)',
    title: '\u00c9valuations',
    text: 'Consulter les \u00e9valuations',
    roles: [Authority.ADMIN, Authority.MANAGER, Authority.EXPERT],
  },
  {
    to: '/test',
    icon: faListAlt,
    gradient: 'linear-gradient(135deg, #b45309, #f59e0b)',
    title: 'Tests',
    text: 'Cr\u00e9er et g\u00e9rer les tests',
    roles: [Authority.ADMIN, Authority.MANAGER],
  },
  {
    to: '/competence',
    icon: faTrophy,
    gradient: 'linear-gradient(135deg, #047857, #10b981)',
    title: 'Comp\u00e9tences',
    text: 'R\u00e9f\u00e9rentiel des comp\u00e9tences',
    roles: [Authority.ADMIN, Authority.EXPERT],
  },
  {
    to: '/question',
    icon: faQuestionCircle,
    gradient: 'linear-gradient(135deg, #b91c1c, #ef4444)',
    title: 'Questions',
    text: 'Banque de questions',
    roles: [Authority.ADMIN, Authority.MANAGER],
  },
  {
    to: '/poste',
    icon: faBriefcase,
    gradient: 'linear-gradient(135deg, #334155, #64748b)',
    title: 'Postes',
    text: 'G\u00e9rer les postes',
    roles: [Authority.ADMIN, Authority.RH],
  },
  {
    to: '/score',
    icon: faChartBar,
    gradient: 'linear-gradient(135deg, #92400e, #d97706)',
    title: 'Scores',
    text: 'Consulter les r\u00e9sultats',
    roles: [Authority.ADMIN, Authority.RH],
  },
  {
    to: '/reponse',
    icon: faClipboardCheck,
    gradient: 'linear-gradient(135deg, #475569, #94a3b8)',
    title: 'R\u00e9ponses',
    text: 'Historique des r\u00e9ponses',
    roles: [Authority.ADMIN, Authority.MANAGER, Authority.EXPERT, Authority.EMPLOYEE],
  },
];

const Dashboard = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const authorities: string[] = account?.authorities ?? [];
  const [stats, setStats] = useState<DashboardData | null>(null);
  const [statsError, setStatsError] = useState(false);

  const has = (...roles: string[]) => hasAnyAuthority(authorities, roles);
  const showStats = has(Authority.ADMIN, Authority.RH, Authority.MANAGER);

  const primaryRole = [Authority.ADMIN, Authority.RH, Authority.MANAGER, Authority.EXPERT, Authority.EMPLOYEE].find(r =>
    authorities.includes(r),
  );
  const roleMeta = primaryRole ? ROLE_META[primaryRole] : null;

  useEffect(() => {
    if (isAuthenticated && showStats) {
      getDashboardStats()
        .then(res => setStats(res.data))
        .catch(() => setStatsError(true));
    }
  }, [isAuthenticated, showStats]);

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

  const visibleCards = QUICK_CARDS.filter(c => has(...c.roles));

  return (
    <div>
      {/* Header */}
      <div className="dashboard-header">
        <div className="d-flex align-items-center flex-wrap gap-3 mb-1">
          <h2 className="dashboard-greeting mb-0">
            Bonjour, <span style={{ color: roleMeta?.color ?? '#4f46e5' }}>{account?.login || 'utilisateur'}</span>
          </h2>
          {roleMeta && (
            <Badge pill className="role-badge" style={{ background: roleMeta.color, fontSize: '0.8rem', padding: '0.45em 1em' }}>
              <FontAwesomeIcon icon={roleMeta.icon} className="me-1" />
              {roleMeta.label}
            </Badge>
          )}
        </div>
        <p className="dashboard-subtitle">{roleMeta?.subtitle ?? 'Voici un aper\u00e7u de votre espace'}</p>
      </div>

      {/* KPI Cards — ADMIN / RH / MANAGER only */}
      {showStats && stats && (
        <>
          <Row className="mb-4 g-3">
            {has(Authority.ADMIN, Authority.RH) && (
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
            )}
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

      {showStats && statsError && (
        <Alert variant="warning" className="mb-4" style={{ borderRadius: '1rem', border: 'none' }}>
          Impossible de charger les statistiques. V&eacute;rifiez vos permissions.
        </Alert>
      )}

      {/* Welcome card for EXPERT / EMPLOYEE (no KPI stats) */}
      {!showStats && roleMeta && (
        <Card className="welcome-card mb-4">
          <Card.Body className="d-flex align-items-center gap-4 flex-wrap">
            <div className="welcome-icon-wrapper" style={{ background: `${roleMeta.color}15` }}>
              <FontAwesomeIcon icon={roleMeta.icon} size="2x" style={{ color: roleMeta.color }} />
            </div>
            <div>
              <h4 className="mb-1" style={{ fontWeight: 700, color: '#1e293b' }}>
                Bienvenue sur SkillTestApp
              </h4>
              <p className="mb-0" style={{ color: '#64748b' }}>
                {primaryRole === Authority.EXPERT
                  ? 'D\u00e9finissez les comp\u00e9tences et participez aux \u00e9valuations des employ\u00e9s.'
                  : 'Consultez vos \u00e9valuations et passez vos tests de comp\u00e9tences.'}
              </p>
            </div>
          </Card.Body>
        </Card>
      )}

      {/* Quick Access — filtered by role */}
      {visibleCards.length > 0 && (
        <>
          <div className="quick-access-title">
            <FontAwesomeIcon icon={faRocket} />
            Acc&egrave;s rapide
          </div>
          <Row className="g-3">
            {visibleCards.map(card => (
              <Col lg={4} md={6} key={card.to}>
                <Card as={Link} to={card.to} className="quick-card h-100">
                  <Card.Body className="text-center">
                    <div className="quick-icon" style={{ background: card.gradient }}>
                      <FontAwesomeIcon icon={card.icon} style={{ color: '#fff' }} />
                    </div>
                    <Card.Title>{card.title}</Card.Title>
                    <Card.Text>{card.text}</Card.Text>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </>
      )}
    </div>
  );
};

export default Dashboard;
