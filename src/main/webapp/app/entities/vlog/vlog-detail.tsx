import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './vlog.reducer';

export const VlogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const vlogEntity = useAppSelector(state => state.vlog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="vlogDetailsHeading">
          <Translate contentKey="myvideoapplicationApp.vlog.detail.title">Vlog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{vlogEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="myvideoapplicationApp.vlog.name">Name</Translate>
            </span>
          </dt>
          <dd>{vlogEntity.name}</dd>
          <dt>
            <span id="handle">
              <Translate contentKey="myvideoapplicationApp.vlog.handle">Handle</Translate>
            </span>
          </dt>
          <dd>{vlogEntity.handle}</dd>
          <dt>
            <Translate contentKey="myvideoapplicationApp.vlog.user">User</Translate>
          </dt>
          <dd>{vlogEntity.user ? vlogEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/vlog" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/vlog/${vlogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default VlogDetail;
