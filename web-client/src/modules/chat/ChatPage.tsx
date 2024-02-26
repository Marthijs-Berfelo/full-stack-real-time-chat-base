import React, { JSX } from 'react';
import { ChatContextProvider } from './components/ChatContext';
import { Flex } from 'antd';
import { UserList } from './components/UserList';
import { ChatDetails } from './components/ChatDetails';
import { MessageForm } from './components/MessageForm';

export default function ChatPage(): JSX.Element {
  return (
    <ChatContextProvider>
      <Flex vertical className="w-screen h-full">
        <Flex className="h-full w-1/2 min-w-80">
          <UserList />
        </Flex>
        <Flex>
          <ChatDetails />
        </Flex>
      </Flex>
      <Flex vertical className="w-screen h-48">
        <MessageForm />
      </Flex>
    </ChatContextProvider>
  )
}