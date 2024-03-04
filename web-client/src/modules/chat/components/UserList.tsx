import React, { JSX } from 'react';
import { useChat } from './ChatContext';
import { Button, Col, Row, Typography } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
const { Text } = Typography;

export function UserList(): JSX.Element {
  const { users, usersLoading, onLoadUsers, onSelectUser } = useChat();
  const { t } = useTranslation(['chat']);

  return (
    <Col className="w-full h-full">
      <Row justify="space-evenly">
        <Button
          className="text-lg"
          icon={<ReloadOutlined />}
          onClick={onLoadUsers}
          loading={usersLoading}
        />
        <Text className="text-2xl">{t('chat:section.user-list')}</Text>
        <Button className="text-lg">{t('chat:user.me')}</Button>
      </Row>
      {users.map(user => (
        <Text key={user.id} className=" m-3 text-lg" onClick={() => onSelectUser(user)}>
          {user.nickName}
        </Text>
      ))}
    </Col>
  );
}
