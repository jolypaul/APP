import './header.scss';

import React from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import { Storage } from 'react-jhipster';

import LoadingBar from 'react-redux-loading-bar';

import { useAppDispatch } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { Authority } from 'app/shared/jhipster/constants';
import { AccountMenu, AdminMenu, LocaleMenu } from '../menus';
import { NavLink } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTachometerAlt, faUserSecret, faUsers, faClipboardCheck, faListAlt, faCogs } from '@fortawesome/free-solid-svg-icons';

import { Brand, Home } from './header-components';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  authorities: string[];
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
}

const Header = (props: IHeaderProps) => {
  const dispatch = useAppDispatch();

  const handleLocaleChange = langKey => {
    Storage.session.set('locale', langKey);
    dispatch(setLocale(langKey));
  };

  const has = (...roles: string[]) => hasAnyAuthority(props.authorities, roles);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <Navbar data-cy="navbar" data-bs-theme="dark" expand="md" fixed="top" className="jh-navbar" collapseOnSelect>
        <Navbar.Toggle aria-controls="header-tabs" aria-label="Menu" />
        <Brand />
        <Navbar.Collapse id="header-tabs">
          <Nav className="ms-auto">
            <Home />
            {props.isAuthenticated && (
              <Nav.Link as={NavLink} to="/dashboard" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faTachometerAlt} className="me-1" />
                <span>Dashboard</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && has(Authority.ADMIN, Authority.MANAGER) && (
              <Nav.Link as={NavLink} to="/discret-evaluation" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faUserSecret} className="me-1" />
                <span>Mode Discret</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && has(Authority.ADMIN, Authority.RH) && (
              <Nav.Link as={NavLink} to="/employee" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faUsers} className="me-1" />
                <span>Employ&eacute;s</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && has(Authority.ADMIN, Authority.MANAGER, Authority.EXPERT) && (
              <Nav.Link as={NavLink} to="/evaluation" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faClipboardCheck} className="me-1" />
                <span>&Eacute;valuations</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && has(Authority.ADMIN, Authority.MANAGER) && (
              <Nav.Link as={NavLink} to="/test" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faListAlt} className="me-1" />
                <span>Tests</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && has(Authority.ADMIN, Authority.EXPERT) && (
              <Nav.Link as={NavLink} to="/competence" className="d-flex align-items-center">
                <FontAwesomeIcon icon={faCogs} className="me-1" />
                <span>Comp&eacute;tences</span>
              </Nav.Link>
            )}
            {props.isAuthenticated && props.isAdmin && <AdminMenu showOpenAPI={props.isOpenAPIEnabled} />}
            <LocaleMenu currentLocale={props.currentLocale} onClick={handleLocaleChange} />
            <AccountMenu isAuthenticated={props.isAuthenticated} />
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
