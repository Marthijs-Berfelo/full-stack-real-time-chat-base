export interface ConversationRegistration {
  peerUserId: string;
}

export interface Conversation {
  id: string,
  users: string[];
  messages: ChatMessage[];
}

export interface ChatMessage {
  id?: string;
  from: string;
  to: string;
  sentAt?: string;
  conversationId: string;
  message: string;
}