import dayjs from 'dayjs';

import { ICompetence } from 'app/shared/model/competence.model';
import { Statut } from 'app/shared/model/enumerations/statut.model';
import { IEvaluation } from 'app/shared/model/evaluation.model';

export interface IScore {
  id?: number;
  valeur?: number;
  pourcentage?: number;
  statut?: keyof typeof Statut;
  dateCalcul?: dayjs.Dayjs;
  evaluation?: IEvaluation | null;
  competence?: ICompetence | null;
}

export const defaultValue: Readonly<IScore> = {};
