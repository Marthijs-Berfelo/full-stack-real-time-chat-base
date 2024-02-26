import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { toFormData, WWW_FORM_HEADER } from './api-utils';
import { AuthTokens } from './account/models';

// @ts-expect-error no-env-on-window
const config = window.env;
const accessTokenKey = 'access_token';
const refreshTokenKey = 'refresh_token';


function login(username: string, password: string): Promise<TokenResponse> {
  return requestToken(
    {
      client_id: config.idp.clientId,
      grant_type: 'password',
      username,
      password
    } as never
  )
    .then(updateAuth)
}

function refreshToken(): Promise<TokenResponse> {
  return requestToken(
    {
      client_id: config.idp.clientId,
      grant_type: 'refresh_token',
      [refreshTokenKey]: localStorage.getItem(refreshTokenKey)
    } as never
  )
    .then(updateAuth)
}

function logout(): Promise<void> {
  const body = toFormData(
    {
      client_id: config.idp.clientId,
      [refreshTokenKey]: localStorage.getItem(refreshTokenKey)
    } as never
  );
  return axios.post(
    `${config.idp.url}/realms/${config.idp.realm}/protocol/openid-connect/logout`,
    body,
    { headers: WWW_FORM_HEADER },
  )
    .then(clearAuth)
}

function getToken(): string | undefined {
  return localStorage.getItem(accessTokenKey) ?? undefined
}

function toTokenResponse(authTokens: AuthTokens): TokenResponse {
  return {
    access_token: authTokens.accessToken,
    expires_in: authTokens.expiresIn,
    refresh_token: authTokens.refreshToken,
    refresh_expires_in: authTokens.refreshExpiresIn,
    token_type: authTokens.tokenType,
    scope: authTokens.scope,
    session_state: authTokens.sessionState,
  };
}

function extractPrincipal(token: TokenResponse): Principal {
  const decoded = jwtDecode<AuthUser>(token.access_token);
  return {
    userId: decoded.sub,
    firsName: decoded.given_name,
    lastName: decoded.family_name,
    middleName: decoded.middle_name,
    email: decoded.email,
    nickName: decoded.nick_name,
    chatUserId: decoded.chat_user_id
  }
}

const idp = {
  login,
  logout,
  refreshToken,
  extractPrincipal,
  toTokenResponse,
}

export { idp, getToken };

function updateAuth(token: TokenResponse): TokenResponse {
  localStorage.setItem(accessTokenKey, token.access_token);
  localStorage.setItem(refreshTokenKey, token.refresh_token);
  return token;
}

function clearAuth(): void {
  localStorage.removeItem(accessTokenKey);
  localStorage.removeItem(refreshTokenKey)
}

function requestToken(body: never): Promise<TokenResponse> {
  return axios.post<TokenResponse>(
    `${config.idp.url}/realms/${config.idp.realm}/protocol/openid-connect/token`,
    toFormData(body),
    { headers: WWW_FORM_HEADER },
  )
    .then((res) => res.data)
}

export interface TokenResponse {
  access_token: string;
  expires_in: number;
  refresh_expires_in: number;
  refresh_token: string;
  token_type: string;
  session_state: string;
  scope: string;
}

export interface Principal {
  userId: string;
  firsName: string;
  lastName: string;
  middleName?: string;
  email: string;
  nickName?: string;
  chatUserId?: string;
}

interface AuthUser {
  email: string;
  preferred_username: string;
  given_name: string;
  family_name: string;
  middle_name?: string;
  nick_name?: string;
  chat_user_id?: string;
  sub: string;
}
