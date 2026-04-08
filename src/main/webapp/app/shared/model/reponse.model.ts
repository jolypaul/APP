import dayjs from 'dayjs';

import { IEvaluation } from 'app/shared/model/evaluation.model';
import { IQuestion } from 'app/shared/model/question.model';

export interface IReponse {
  id?: number;
  contenu?: string;
  estCorrecte?: boolean | null;
  dateReponse?: dayjs.Dayjs;
  commentaireManager?: string | null;
  question?: IQuestion | null;
  evaluation?: IEvaluation | null;
}

export const defaultValue: Readonly<IReponse> = {
  estCorrecte: false,
};
