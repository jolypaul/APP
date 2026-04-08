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

import { getEntities } from './reponse.reducer';

export const Reponse = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const reponseList = useAppSelector(state => state.reponse.entities);
  const loading = useAppSelector(state => state.reponse.loading);
  const totalItems = useAppSelector(state => state.reponse.totalItems);

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
      <h2 id="reponse-heading" data-cy="ReponseHeading">
        <Translate contentKey="skillTestApp.reponse.home.title">Reponses</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="skillTestApp.reponse.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/reponse/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="skillTestApp.reponse.home.createLabel">Create new Reponse</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {reponseList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="skillTestApp.reponse.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('contenu')}>
                  <Translate contentKey="skillTestApp.reponse.contenu">Contenu</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contenu')} />
                </th>
                <th className="hand" onClick={sort('estCorrecte')}>
                  <Translate contentKey="skillTestApp.reponse.estCorrecte">Est Correcte</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('estCorrecte')} />
                </th>
                <th className="hand" onClick={sort('dateReponse')}>
                  <Translate contentKey="skillTestApp.reponse.dateReponse">Date Reponse</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dateReponse')} />
                </th>
                <th className="hand" onClick={sort('commentaireManager')}>
                  <Translate contentKey="skillTestApp.reponse.commentaireManager">Commentaire Manager</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('commentaireManager')} />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.reponse.question">Question</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="skillTestApp.reponse.evaluation">Evaluation</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {reponseList.map(reponse => (
                <tr key={`entity-${reponse.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/reponse/${reponse.id}`} variant="link" size="sm">
                      {reponse.id}
                    </Button>
                  </td>
                  <td>{reponse.contenu}</td>
                  <td>{reponse.estCorrecte ? 'true' : 'false'}</td>
                  <td>{reponse.dateReponse ? <TextFormat type="date" value={reponse.dateReponse} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{reponse.commentaireManager}</td>
                  <td>{reponse.question ? <Link to={`/question/${reponse.question.id}`}>{reponse.question.enonce}</Link> : ''}</td>
                  <td>{reponse.evaluation ? <Link to={`/evaluation/${reponse.evaluation.id}`}>{reponse.evaluation.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/reponse/${reponse.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/reponse/${reponse.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/reponse/${reponse.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="skillTestApp.reponse.home.notFound">No Reponses found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={reponseList && reponseList.length > 0 ? '' : 'd-none'}>
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

export default Reponse;
