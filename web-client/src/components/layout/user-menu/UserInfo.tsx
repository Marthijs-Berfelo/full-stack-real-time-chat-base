import React from 'react';
import { Principal } from '../../../api/idp';
import { Flex, Typography } from 'antd';
const { Text } = Typography

export function UserInfo({ user }: { user: Principal }) {
  return (
    <Flex>
      <Flex vertical>
        <Text>{user.firsName}</Text>
        <Text>{user.middleName}</Text>
        <Text>{user.lastName}</Text>
      </Flex>
      <Flex vertical>
        <Text>{user.email}</Text>
      </Flex>
    </Flex>
  );
}