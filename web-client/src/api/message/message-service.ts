import api from '../api';
import { ChatMessage, Conversation } from './models';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error

const BASE_URL = `${window.env.messageService}/api`;
const MESSAGES_PATH = '/messages';
const CONVERSATIONS_PATH = '/conversations';

const http = api(BASE_URL);

function initializeConversation(peerUserId: string): Promise<Conversation> {
  return http.postSecure(CONVERSATIONS_PATH, { peerUserId });
}

function removeConversations(): Promise<void> {
  return http.deleteSecure(CONVERSATIONS_PATH);
}

function sendMessage(message: ChatMessage): Promise<ChatMessage> {
  return http.postSecure(`${CONVERSATIONS_PATH}/${message.conversationId}`, message);
}

function getMessages(): Promise<ChatMessage[]> {
  return http.getSecure(MESSAGES_PATH);
}

export const messageService = {
  initializeConversation,
  removeConversations,
  sendMessage,
  getMessages,
};
