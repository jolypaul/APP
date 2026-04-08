import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './score.reducer';

export const Score = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const scoreList = useAppSelector(state => state.score.entities);
  const loading = useAppSelector(state => state.score.loading);
  const totalItems = useAppSelector(state => state.score.totalItems);

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
      <h2 id="score-heading" data-cy="ScoreHeading">
        <Translate contentKey="skillTestApp.score.home.title">Scores</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="skillTestApp.score.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/score/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="skillTestApp.score.home.createLabel">Create new Score</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {scoreList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="skillTestApp.score.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('valeur')}>
                  <Translate contentKey="skillTestApp.score.valeur">Valeur</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('valeur')} />
                </th>
                <th className="hand" onClick={sort('pourcentage')}>
                  <Translate contentKey="skillTestApp.score.pourcentage">Pourcentage</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pourcentage')} />
                </th>
                <th className="hand" onClick={sort('statut')}>
                  <Translate contentKey="skillTestApp.score.statut">Statut</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('statut')} />
                </th>
                <th className="hand" onClick={sort('dateCalcul')}>
                  <Translate contentKey="skillTestApp.score.dateCalcul">Date Calcul</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dateCalcul')} />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.score.evaluation">Evaluation</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.score.competence">Competence</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {scoreList.map(score => (
                <tr key={`entity-${score.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/score/${score.id}`} variant="link" size="sm">
                      {score.id}
                    </Button>
                  </td>
                  <td>{score.valeur}</td>
                  <td>{score.pourcentage}</td>
                  <td>
                    <Translate contentKey={`skillTestApp.Statut.${score.statut}`} />
                  </td>
                  <td>{score.dateCalcul ? <TextFormat type="date" value={score.dateCalcul} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{score.evaluation ? <Link to={`/evaluation/${score.evaluation.id}`}>{score.evaluation.id}</Link> : ''}</td>
                  <td>{score.competence ? <Link to={`/competence/${score.competence.id}`}>{score.competence.nom}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/score/${score.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/score/${score.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/score/${score.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="skillTestApp.score.home.notFound">No Scores found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={scoreList && scoreList.length > 0 ? '' : 'd-none'}>
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

export default Score;
