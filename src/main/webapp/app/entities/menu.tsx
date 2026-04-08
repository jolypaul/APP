import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/employee">
        <Translate contentKey="global.menu.entities.employee" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/poste">
        <Translate contentKey="global.menu.entities.poste" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/competence">
        <Translate contentKey="global.menu.entities.competence" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/test">
        <Translate contentKey="global.menu.entities.test" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/question">
        <Translate contentKey="global.menu.entities.question" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reponse">
        <Translate contentKey="global.menu.entities.reponse" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/evaluation">
        <Translate contentKey="global.menu.entities.evaluation" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/score">
        <Translate contentKey="global.menu.entities.score" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
