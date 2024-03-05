import { AccountRegistration, AuthTokens, ChatAccountRegistration } from './models';
import api from '../api';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
const BASE_URL = `${window.env.accountService}/api/account`;
const CHAT_PATH = '/chat';

const http = api(BASE_URL);

function registerAccount(registration: AccountRegistration): Promise<void> {
  return http.post('', registration);
}

function registerChatAccount(registration: ChatAccountRegistration): Promise<AuthTokens> {
  return http.post<ChatAccountRegistration, AuthTokens>(CHAT_PATH, registration);
}

function removeChatAccount(): Promise<void> {
  return http.deleteSecure(CHAT_PATH);
}

function updateChatUserId(chatUserId: string): Promise<void> {
  return http.putSecure(`${CHAT_PATH}/${chatUserId}`);
}

export const accountService = {
  registerAccount,
  registerChatAccount,
  removeChatAccount,
  updateChatUserId,
};
