import './footer.scss';

import React from 'react';

const Footer = () => (
  <footer className="app-footer">
    <div className="footer-content">
      <div className="footer-brand">
        <span className="footer-logo">SkillTestApp</span>
        <span className="footer-separator">|</span>
        <span className="footer-tagline">&Eacute;valuation des comp&eacute;tences</span>
      </div>
      <div className="footer-author">
        Con&ccedil;u par <strong>EYENGA NTSAMA</strong>
      </div>
      <div className="footer-copy">&copy; {new Date().getFullYear()} Tous droits r&eacute;serv&eacute;s</div>
    </div>
  </footer>
);

export default Footer;
