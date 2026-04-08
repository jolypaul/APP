import axios from 'axios';

const apiUrl = '/api/discret-evaluation';

export const startDiscretEvaluation = (data: { employeeId: number; testId: number; managerId: number }) => {
  return axios.post(`${apiUrl}/start`, data);
};

export const submitAnswerManager = (data: {
  evaluationId: number;
  questionId: number;
  contenu: string;
  estCorrecte: boolean;
  commentaireManager?: string;
}) => {
  return axios.post(`${apiUrl}/submit-answer`, data);
};

export const finalizeEvaluation = (evaluationId: number) => {
  return axios.post(`${apiUrl}/finalize/${evaluationId}`);
};

export const getDashboardStats = () => {
  return axios.get(`${apiUrl}/dashboard`);
};
