import dayjs from 'dayjs';

import { Role } from 'app/shared/model/enumerations/role.model';
import { IPoste } from 'app/shared/model/poste.model';

export interface IEmployee {
  id?: number;
  matricule?: string;
  nom?: string;
  prenom?: string;
  email?: string;
  telephone?: string | null;
  dateEmbauche?: dayjs.Dayjs;
  role?: keyof typeof Role;
  hasUserAccount?: boolean | null;
  canLogin?: boolean | null;
  defaultPasswordHint?: string | null;
  poste?: IPoste | null;
}

export const defaultValue: Readonly<IEmployee> = {};
