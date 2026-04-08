import dayjs from 'dayjs';

import { IEmployee } from 'app/shared/model/employee.model';
import { EvaluationStatus } from 'app/shared/model/enumerations/evaluation-status.model';
import { Statut } from 'app/shared/model/enumerations/statut.model';
import { TestMode } from 'app/shared/model/enumerations/test-mode.model';
import { ITest } from 'app/shared/model/test.model';

export interface IEvaluation {
  id?: number;
  dateEvaluation?: dayjs.Dayjs;
  status?: keyof typeof EvaluationStatus;
  mode?: keyof typeof TestMode;
  scoreTotal?: number | null;
  remarques?: string | null;
  statut?: keyof typeof Statut | null;
  employee?: IEmployee | null;
  test?: ITest | null;
  manager?: IEmployee | null;
}

export const defaultValue: Readonly<IEvaluation> = {};
