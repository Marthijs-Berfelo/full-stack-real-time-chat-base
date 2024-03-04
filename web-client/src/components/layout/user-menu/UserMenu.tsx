import React from 'react';
import { Flex, Layout } from 'antd';
import { useLayout } from '../LayoutContext';
import { useAuth } from '../../AuthContext';
import { UserInfo } from './UserInfo';
import { LoginForm } from './LoginForm';
import { CloseOutlined } from '@ant-design/icons';

const { Sider } = Layout;

export function UserMenu() {
  const { showUserMenu, toggleUserMenu } = useLayout();
  const { currentUser } = useAuth();

  return (
    <Sider
      className="bg-emerald-200"
      collapsedWidth={0}
      collapsible
      trigger={null}
      collapsed={!showUserMenu}
    >
      <Flex className="px-2 pt-2">
        <CloseOutlined className="text-lg" onClick={toggleUserMenu} />
      </Flex>
      <Flex align="center" justify="center" className="px-2 pt-12">
        {currentUser ? <UserInfo user={currentUser} /> : <LoginForm />}
      </Flex>
    </Sider>
  );
}
