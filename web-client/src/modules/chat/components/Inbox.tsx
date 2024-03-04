import React, { JSX } from 'react';
import { useChat } from './ChatContext';
import { useTranslation } from 'react-i18next';
import { Badge, Flex, Typography } from 'antd';

const { Text } = Typography;

export function Inbox(): JSX.Element {
  const { conversations, onSelectConversation } = useChat();
  const { t } = useTranslation(['chat']);

  return (
    <Flex className="w-full h-full">
      <Flex vertical justify="space-evenly">
        <Text className="text-2xl">{t('chat:section.inbox')}</Text>
      </Flex>
      {conversations.map(conversation => (
        <Text
          key={conversation.peerId}
          className="m-3 text-lg"
          onClick={() => onSelectConversation(conversation)}
        >
          {conversation.user ? conversation.user.nickName : t('chat:user.off-line')}
          <Badge count={conversation.newMessages} />
        </Text>
      ))}
    </Flex>
  );
}
