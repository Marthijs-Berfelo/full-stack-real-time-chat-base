import api from '../api';
import { ChatRegistration, ChatUser } from './models';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
const BASE_URL = `${window.env.userService}/api/users`;
const VALIDATE_PATH = '/validate';

const http = api(BASE_URL);

function getUsers(): Promise<ChatUser[]> {
  return http.getSecure('');
}

function register(registration: ChatRegistration): Promise<ChatUser> {
  return http.post('', registration);
}

function validateNickName(nickName: string): Promise<boolean> {
  const params = {
    'nick-name': nickName,
  };
  return http
    .get(VALIDATE_PATH, params)
    .then(() => true)
    .catch(() => false);
}

async function removeUser(): Promise<void> {
  await http.deleteSecure('');
}

export const userService = {
  getUsers,
  register,
  validateNickName,
  removeUser,
};
