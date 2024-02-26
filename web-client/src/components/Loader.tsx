import React, { JSX } from 'react';
import { Col, Row, Spin } from 'antd';

const Loader = (): JSX.Element => (
  <Row justify={'center'} align={'middle'} className="w-full h-full">
    <Col>
      <Spin />
    </Col>
  </Row>
);

export default Loader;