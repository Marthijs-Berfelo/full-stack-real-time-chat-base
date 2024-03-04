import React from 'react';
import { Avatar, Button, Col, Layout, Row } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { useLayout } from './LayoutContext';
import { useAuth } from '../AuthContext';

const { Header } = Layout;

export function PageHeader() {
  const { toggleUserMenu, showUserMenu } = useLayout();
  return (
    <Header className="bg-emerald-400 px-0 z-10 h-16">
      <Row justify="space-between" align="top">
        <Col />
        <Col />
        <Col className="flex">
          <div>
            {!showUserMenu && (
              <Button
                icon={<UserIcon />}
                onClick={toggleUserMenu}
                type="text"
                className="text-sm mr-2"
              />
            )}
          </div>
        </Col>
      </Row>
    </Header>
  );
}

function UserIcon() {
  const { currentUser } = useAuth();
  if (!currentUser) {
    return (
      <Avatar className="bg-indigo-500" icon={<UserOutlined className="text-lg text-white" />} />
    );
  } else {
    return (
      <Avatar className="bg-indigo-500">
        {currentUser.firsName.charAt(0)}
        {currentUser.lastName.charAt(0)}
      </Avatar>
    );
  }
}
