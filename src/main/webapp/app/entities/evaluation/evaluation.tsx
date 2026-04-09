import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faPause, faPlay, faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './evaluation.reducer';

export const Evaluation = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const evaluationList = useAppSelector(state => state.evaluation.entities);
  const loading = useAppSelector(state => state.evaluation.loading);
  const totalItems = useAppSelector(state => state.evaluation.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const handleSuspend = async (evaluationId: number) => {
    await axios.post(`/api/discret-evaluation/suspend/${evaluationId}`);
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="evaluation-heading" data-cy="EvaluationHeading">
        <Translate contentKey="skillTestApp.evaluation.home.title">Evaluations</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="skillTestApp.evaluation.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/evaluation/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="skillTestApp.evaluation.home.createLabel">Create new Evaluation</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {evaluationList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="skillTestApp.evaluation.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('dateEvaluation')}>
                  <Translate contentKey="skillTestApp.evaluation.dateEvaluation">Date Evaluation</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dateEvaluation')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="skillTestApp.evaluation.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('mode')}>
                  <Translate contentKey="skillTestApp.evaluation.mode">Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mode')} />
                </th>
                <th className="hand" onClick={sort('scoreTotal')}>
                  <Translate contentKey="skillTestApp.evaluation.scoreTotal">Score Total</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('scoreTotal')} />
                </th>
                <th className="hand" onClick={sort('remarques')}>
                  <Translate contentKey="skillTestApp.evaluation.remarques">Remarques</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remarques')} />
                </th>
                <th className="hand" onClick={sort('statut')}>
                  <Translate contentKey="skillTestApp.evaluation.statut">Statut</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('statut')} />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.evaluation.employee">Employee</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.evaluation.test">Test</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.evaluation.manager">Manager</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {evaluationList.map(evaluation => (
                <tr key={`entity-${evaluation.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/evaluation/${evaluation.id}`} variant="link" size="sm">
                      {evaluation.id}
                    </Button>
                  </td>
                  <td>
                    {evaluation.dateEvaluation ? (
                      <TextFormat type="date" value={evaluation.dateEvaluation} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    <Translate contentKey={`skillTestApp.EvaluationStatus.${evaluation.status}`} />
                  </td>
                  <td>
                    <Translate contentKey={`skillTestApp.TestMode.${evaluation.mode}`} />
                  </td>
                  <td>{evaluation.scoreTotal}</td>
                  <td>{evaluation.remarques}</td>
                  <td>
                    <Translate contentKey={`skillTestApp.Statut.${evaluation.statut}`} />
                  </td>
                  <td>{evaluation.employee ? <Link to={`/employee/${evaluation.employee.id}`}>{evaluation.employee.nom}</Link> : ''}</td>
                  <td>{evaluation.test ? <Link to={`/test/${evaluation.test.id}`}>{evaluation.test.titre}</Link> : ''}</td>
                  <td>{evaluation.manager ? <Link to={`/employee/${evaluation.manager.id}`}>{evaluation.manager.nom}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/evaluation/${evaluation.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      {(evaluation.status === 'EN_COURS' || evaluation.status === 'SUSPENDUE') && evaluation.mode === 'DISCRET' && (
                        <Button as={Link as any} to={`/discret-evaluation?resume=${evaluation.id}`} variant="success" size="sm">
                          <FontAwesomeIcon icon={faPlay} /> <span className="d-none d-md-inline">Reprendre</span>
                        </Button>
                      )}
                      {evaluation.status === 'EN_COURS' && evaluation.mode === 'DISCRET' && (
                        <Button variant="warning" size="sm" onClick={() => handleSuspend(evaluation.id)}>
                          <FontAwesomeIcon icon={faPause} /> <span className="d-none d-md-inline">Suspendre</span>
                        </Button>
                      )}
                      <Button
                        onClick={() =>
                          (window.location.href = `/evaluation/${evaluation.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="skillTestApp.evaluation.home.notFound">No Evaluations found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={evaluationList && evaluationList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Evaluation;
