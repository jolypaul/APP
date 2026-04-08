import { ICompetence } from 'app/shared/model/competence.model';
import { Level } from 'app/shared/model/enumerations/level.model';

export interface IPoste {
  id?: number;
  intitule?: string;
  description?: string | null;
  niveauRequis?: keyof typeof Level;
  competenceses?: ICompetence[] | null;
}

export const defaultValue: Readonly<IPoste> = {};
