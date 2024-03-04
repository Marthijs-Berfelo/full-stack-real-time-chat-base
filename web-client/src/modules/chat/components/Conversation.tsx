import React, { JSX } from 'react';
import { Flex, Typography } from 'antd';
import { useTranslation } from 'react-i18next';
import { ChatConversation } from './ChatContext';
import { ChatMessage } from '../../../api/message/models';

const { Text } = Typography;

interface ConversationProps {
  selectedUser: ChatConversation;
}

export function Conversation({ selectedUser }: ConversationProps): JSX.Element {
  const { t } = useTranslation(['chat']);

  return (
    <Flex className="w-full h-full">
      <Flex vertical justify="space-evenly">
        <Text className="text-2xl">{t('chat:section.user-profile')}</Text>
      </Flex>
      <Flex vertical justify="space-evenly">
        <Text className="text-xl">{selectedUser.user?.nickName ?? t('chat:user.off-line')}</Text>
      </Flex>
      {selectedUser.messages.map(message => (
        <Message key={message.id} peerId={selectedUser.peerId} message={message} />
      ))}
    </Flex>
  );
}

interface MessageProps {
  peerId: string;
  message: ChatMessage;
}

function Message({ peerId, message }: MessageProps): JSX.Element {
  return (
    <Flex vertical justify={message.from === peerId ? 'start' : 'end'} className="w-full">
      <Flex className="w-2/3 bg-emerald-200" align={message.from === peerId ? 'start' : 'end'}>
        <Text className="text-lg">{message.message}</Text>
        <Text className="text-sm">{message.sentAt}</Text>
      </Flex>
    </Flex>
  );
}
