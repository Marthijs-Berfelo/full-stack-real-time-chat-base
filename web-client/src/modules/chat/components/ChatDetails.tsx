import React, { JSX } from 'react';
import { useChat } from './ChatContext';
import { Conversation } from './Conversation';
import { Inbox } from './Inbox';

export function ChatDetails(): JSX.Element {
  const { selectedUser } = useChat();

  return selectedUser ? (
    <Conversation selectedUser={selectedUser} />
  ) : (
    <Inbox />
  );
}