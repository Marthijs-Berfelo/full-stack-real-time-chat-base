import React, { lazy } from 'react';
import { Layout } from 'antd';
import { useAuth } from '../AuthContext';
const LoginPage = lazy(() => import('../../modules/login/LoginPage'));
const ChatPage = lazy(() => import('../../modules/chat/ChatPage'));
const { Content } = Layout;
export function PageContent() {
  const { currentUser } = useAuth();

  return (
    <Content className="p-3 overflow-x-clip overflow-y-auto">
      {!currentUser ? <LoginPage /> : <ChatPage />}
    </Content>
  );
}
