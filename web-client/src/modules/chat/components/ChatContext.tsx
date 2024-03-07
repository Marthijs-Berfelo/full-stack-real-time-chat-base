import React, {
  createContext,
  JSX,
  PropsWithChildren,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { ChatUser, userService } from '../../../api/user';
import { ChatMessage, messageService } from '../../../api/message';
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
  const [lastMessageAt, setLastMessageAt] = useState<string>();
  const [users, setUsers] = useState<ChatUser[]>([]);
  const [selectedUser, setSelectedUser] = useState<ChatConversation>();
  const [conversations, setConversations] = useState<ChatConversation[]>([]);
  const { chatUser, exitChat } = useAuth();

  useEffect(() => {
    onLoadUsers();
    const timeOut = setTimeout(() => {
      onLoadUsers();
      updateMessages();
    }, 30_000);
    return () => clearTimeout(timeOut);
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
    await userService
      .getUsers()
      .then(setUsers)
      .catch(() => setUsers([]))
      .finally(() => setUsersLoading(false));
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
      await messageService
        .sendMessage(chatMessage)
        .then(chatMessage => {
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
        })
        .finally(() => setSendingMessage(false));
    }
  }

  async function onExit(): Promise<void> {
    await messageService.removeConversations();
    await exitChat();
  }

  async function initializeConversation(peerId: string): Promise<void> {
    await messageService.initializeConversation(peerId).then(conversation => {
      setConversations(previous => {
        const chatConversation = previous.find(val => val.peerId === peerId);
        const conversations = previous.filter(val => val.peerId !== peerId);
        if (chatConversation) {
          chatConversation.conversationId = conversation.id;
          conversations.push(chatConversation);
        }
        return conversations;
      });
      setSelectedUser(previous => {
        if (previous) {
          previous.conversationId = conversation.id;
          return previous;
        } else {
          return {
            peerId,
            conversationId: conversation.id,
            user: chatUser,
            messages: conversation.messages,
            newMessages: 0,
          };
        }
      });
    });
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

  async function updateMessages() {
    await messageService.getMessages(lastMessageAt).then(messages => {
      messages.forEach(handleNewMessage);
      setLastMessageAt(messages.pop()?.sentAt);
    });
  }

  function handleNewMessage(message: ChatMessage): void {
    const conversation = conversations.find(
      conversation => conversation.conversationId === message.conversationId
    ) ?? {
      conversationId: message.conversationId,
      peerId: message.from,
      user: users.find(user => user.id == message.from),
      messages: [],
      newMessages: 0,
    };
    conversation.newMessages = conversation.newMessages + 1;
    conversation.messages.push(message);
    setConversations(previous => [
      conversation,
      ...previous.filter(conv => conv.conversationId !== conversation.conversationId),
    ]);
    if (selectedUser && selectedUser.conversationId === conversation.conversationId) {
      setSelectedUser(conversation);
    }
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
    onExit,
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
  onExit: () => Promise<void>;
}

export interface ChatConversation {
  peerId: string;
  user?: ChatUser;
  conversationId?: string;
  messages: ChatMessage[];
  newMessages: number;
}
