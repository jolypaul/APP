import { Level } from 'app/shared/model/enumerations/level.model';
import { QuestionType } from 'app/shared/model/enumerations/question-type.model';
import { ITest } from 'app/shared/model/test.model';

export interface IQuestion {
  id?: number;
  enonce?: string;
  type?: keyof typeof QuestionType;
  niveau?: keyof typeof Level;
  points?: number;
  choixMultiple?: string | null;
  reponseAttendue?: string | null;
  test?: ITest | null;
}

export const defaultValue: Readonly<IQuestion> = {};
