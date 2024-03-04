import React, { JSX, useEffect, useMemo, useState } from 'react';
import { ChatUser } from '../../../api/user/models';
import { ChatMessage } from '../../../api/message/models';
import { createContext, PropsWithChildren, useContext } from 'react';
import { useAuth } from '../../../components/AuthContext';

export function useChat(): ChatContextType {
  const context = useContext(ChatContext);
  if (context) {
    return context;
  }
  throw new Error('`useChat` must be used with the `ChatContextProvider`');
}

export function ChatContextProvider({ children }: PropsWithChildren): JSX.Element {
  const [usersLoading, setUsersLoading] = useState(false);
  const [sendingMessage, setSendingMessage] = useState(false);
  const [users, setUsers] = useState<ChatUser[]>([]);
  const [selectedUser, setSelectedUser] = useState<ChatConversation>();
  const [conversations, setConversations] = useState<ChatConversation[]>([]);
  const { chatUser } = useAuth();

  useEffect(() => {
    onLoadUsers();
  }, []);

  const totalNewMessages = useMemo(
    () => conversations.map(conv => conv.newMessages).reduce((sum, next) => sum + next, 0),
    [conversations]
  );

  const disableMessageForm = useMemo(
    () => !selectedUser || selectedUser.peerId == chatUser?.id || !selectedUser.user,
    [selectedUser, chatUser]
  );

  async function onLoadUsers(): Promise<void> {
    setUsersLoading(true);
    console.log('TODO: LOAD USERS');
    setUsers([]);
    setUsersLoading(false);
  }

  function onShowInbox(): void {
    setSelectedUser(undefined);
  }

  function onSelectUser(user: ChatUser): void {
    const conversation =
      conversations.find(conversation => conversation.peerId == user.id) ??
      addNewConversation(user);
    setSelectedUser(conversation);
  }

  function onSelectConversation(conversation: ChatConversation): void {
    setSelectedUser(conversation);
  }

  function onShowCurrentUser(): void {
    if (chatUser) {
      setSelectedUser(createNewConversation(chatUser));
    }
  }

  async function onSendMessage(message: string): Promise<void> {
    if (selectedUser) {
      setSendingMessage(true);
      if (!selectedUser.conversationId) {
        await initializeConversation(selectedUser.peerId);
      }
      const chatMessage: ChatMessage = {
        from: chatUser!.id,
        to: selectedUser!.peerId,
        conversationId: selectedUser!.conversationId!,
        message,
      };
      console.log('TODO: SEND MESSAGE', chatMessage);
      setConversations(previous =>
        previous.map(conv => {
          if (conv.conversationId == chatMessage.conversationId) {
            conv.messages.push(chatMessage);
          }
          return conv;
        })
      );
      setSelectedUser(previous => {
        if (previous) {
          previous.messages.push(chatMessage);
        }
        return previous;
      });
    }
  }

  async function initializeConversation(peerId: string): Promise<void> {
    console.log('TODO: INIT CONVERSATION', peerId);
    console.log('TODO: UPDATE CONVERSATIONS', peerId);
    console.log('TODO: UPDATE SELECTED USER', peerId);
  }

  function addNewConversation(user: ChatUser): ChatConversation {
    const conversation = createNewConversation(user);
    setConversations(previous => [...previous, conversation]);
    return conversation;
  }

  function createNewConversation(user: ChatUser): ChatConversation {
    return {
      peerId: user.id,
      user,
      messages: [],
      newMessages: 0,
    };
  }

  const context: ChatContextType = {
    usersLoading,
    sendingMessage,
    totalNewMessages,
    disableMessageForm,
    users,
    selectedUser,
    conversations,
    onLoadUsers,
    onShowInbox,
    onSelectUser,
    onSelectConversation,
    onShowCurrentUser,
    onSendMessage,
  };

  return <ChatContext.Provider value={context}>{children}</ChatContext.Provider>;
}

const ChatContext = createContext<ChatContextType | undefined>(undefined);

interface ChatContextType {
  usersLoading: boolean;
  sendingMessage: boolean;
  totalNewMessages: number;
  disableMessageForm: boolean;
  users: ChatUser[];
  selectedUser?: ChatConversation;
  conversations: ChatConversation[];
  onLoadUsers: () => Promise<void>;
  onShowInbox: () => void;
  onSelectUser: (user: ChatUser) => void;
  onSelectConversation: (conversation: ChatConversation) => void;
  onShowCurrentUser: () => void;
  onSendMessage: (message: string) => Promise<void>;
}

export interface ChatConversation {
  peerId: string;
  user?: ChatUser;
  conversationId?: string;
  messages: ChatMessage[];
  newMessages: number;
}
