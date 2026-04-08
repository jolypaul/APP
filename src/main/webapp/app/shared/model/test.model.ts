import dayjs from 'dayjs';

import { ICompetence } from 'app/shared/model/competence.model';
import { TestMode } from 'app/shared/model/enumerations/test-mode.model';

export interface ITest {
  id?: number;
  titre?: string;
  description?: string | null;
  mode?: keyof typeof TestMode;
  duree?: number | null;
  dateCreation?: dayjs.Dayjs;
  actif?: boolean;
  competenceses?: ICompetence[] | null;
}

export const defaultValue: Readonly<ITest> = {
  actif: false,
};
