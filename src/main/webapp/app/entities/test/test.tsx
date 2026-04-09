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

import { getEntities } from './test.reducer';

export const Test = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const testList = useAppSelector(state => state.test.entities);
  const loading = useAppSelector(state => state.test.loading);
  const totalItems = useAppSelector(state => state.test.totalItems);

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
      <h2 id="test-heading" data-cy="TestHeading">
        <Translate contentKey="skillTestApp.test.home.title">Tests</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="skillTestApp.test.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/test-builder" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="skillTestApp.test.home.createLabel">Create new Test</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {testList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="skillTestApp.test.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('titre')}>
                  <Translate contentKey="skillTestApp.test.titre">Titre</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('titre')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="skillTestApp.test.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('mode')}>
                  <Translate contentKey="skillTestApp.test.mode">Mode</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('mode')} />
                </th>
                <th className="hand" onClick={sort('duree')}>
                  <Translate contentKey="skillTestApp.test.duree">Duree</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('duree')} />
                </th>
                <th className="hand" onClick={sort('dateCreation')}>
                  <Translate contentKey="skillTestApp.test.dateCreation">Date Creation</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dateCreation')} />
                </th>
                <th className="hand" onClick={sort('actif')}>
                  <Translate contentKey="skillTestApp.test.actif">Actif</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('actif')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {testList.map(test => (
                <tr key={`entity-${test.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/test/${test.id}`} variant="link" size="sm">
                      {test.id}
                    </Button>
                  </td>
                  <td>{test.titre}</td>
                  <td>{test.description}</td>
                  <td>
                    <Translate contentKey={`skillTestApp.TestMode.${test.mode}`} />
                  </td>
                  <td>{test.duree}</td>
                  <td>{test.dateCreation ? <TextFormat type="date" value={test.dateCreation} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{test.actif ? 'true' : 'false'}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/test/${test.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/test-builder/${test.id}`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/test/${test.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="skillTestApp.test.home.notFound">No Tests found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={testList && testList.length > 0 ? '' : 'd-none'}>
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

export default Test;
