import { Level } from 'app/shared/model/enumerations/level.model';
import { IPoste } from 'app/shared/model/poste.model';
import { ITest } from 'app/shared/model/test.model';

export interface ICompetence {
  id?: number;
  nom?: string;
  description?: string | null;
  categorie?: string | null;
  niveauAttendu?: keyof typeof Level;
  posteses?: IPoste[] | null;
  testses?: ITest[] | null;
}

export const defaultValue: Readonly<ICompetence> = {};
