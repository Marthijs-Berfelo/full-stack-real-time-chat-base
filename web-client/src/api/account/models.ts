export interface AccountRegistration {
  firstName: string;
  lastName: string;
  middleName?: string;
  email: string;
  nickName?: string;
  password: string;
  passwordConfirmation: string;
}

export interface ChatAccountRegistration {
  nickName: string;
  chatUserId: string;
  email?: string;
}

export interface AuthTokens {
  accessToken: string;
  expiresIn: number;
  refreshToken: string;
  refreshExpiresIn: number;
  tokenType: string;
  notBefore: string;
  scope: string;
  sessionState: string;
}
