import axios from 'axios';

const apiUrl = '/api/discret-evaluation';

export interface IDiscretQuestion {
  id: number;
  enonce: string;
  type: 'QCM' | 'OUVERTE' | 'VRAI_FAUX' | 'PRATIQUE';
  niveau: string;
  points: number;
  options: string[];
}

export interface IDiscretSession {
  evaluation: {
    id: number;
    manager?: {
      id?: number;
      nom?: string;
      prenom?: string;
    };
  };
  manager?: {
    id?: number;
    nom?: string;
    prenom?: string;
  };
  questions: IDiscretQuestion[];
}

export interface IAnswerCorrectionResult {
  questionId: number;
  estCorrecte: boolean;
  confidence: number;
  commentaireIa: string;
}

export const startDiscretEvaluation = (data: { employeeId: number; testId: number; managerId?: number }) => {
  return axios.post<IDiscretSession>(`${apiUrl}/start`, data);
};

export const submitAnswerManager = (data: { evaluationId: number; questionId: number; contenu: string; commentaireManager?: string }) => {
  return axios.post<IAnswerCorrectionResult>(`${apiUrl}/submit-answer`, data);
};

export const finalizeEvaluation = (evaluationId: number) => {
  return axios.post(`${apiUrl}/finalize/${evaluationId}`);
};

export const getDashboardStats = () => {
  return axios.get(`${apiUrl}/dashboard`);
};
