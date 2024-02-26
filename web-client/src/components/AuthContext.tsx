import { idp, Principal, TokenResponse } from '../api/idp';
import React, { createContext, JSX, PropsWithChildren, useContext, useEffect, useMemo, useState } from 'react';
import { AccountRegistration, ChatAccountRegistration } from '../api/account/models';
import { ChatUser } from '../api/user/models';

export function useAuth(): AuthContextType {

  const context = useContext(AuthContext);
  if (context) {
    return context;
  }
  throw Error('`useAuth` must be used with the `AuthProvider`');
}
export function AuthProvider({ children }: PropsWithChildren): JSX.Element {
  const [loading, setLoading] = useState(false);
  const [token, setToken] = useState<TokenResponse>();
  const [chatUser, setChatUser] = useState<ChatUser>();

  useEffect(() => {
    let refreshInterval: NodeJS.Timeout;
    if (token) {
      refreshInterval = setTimeout(() => {
        idp
          .refreshToken()
          .then(setToken)
          .catch(clearToken)
      }, (token.expires_in - 5) * 1_000);
    }
    return () => clearTimeout(refreshInterval);
  }, [token]);

  const currentUser = useMemo(() => {
    if (token) {
      return idp.extractPrincipal(token)
    }
  }, [token]);

  async function login(username: string, password: string): Promise<void> {

    setLoading(true);
    await idp
      .login(username, password)
      .then(setToken)
      .catch(clearToken)
      .finally(disableLoading);
  }
  async function logout(): Promise<void> {

    setLoading(true);
    await idp
      .logout()
      .then(clearToken)
      .catch(clearToken)
      .finally(disableLoading)
  }

  async function registerAccount(registration: AccountRegistration): Promise<void> {
    console.log('TODO REGISTER:', registration);
  }

  async function registerChatAccount(chatUser: ChatUser): Promise<void> {
    setChatUser(chatUser);
    const chatRegistration: ChatAccountRegistration = {
      nickName: chatUser.nickName,
      chatUserId: chatUser.id,
    };
    console.log('TODO REGISTER:', chatRegistration);
  }

  function disableLoading() {

    setLoading(false);
  }
  function clearToken() {

    setToken(undefined);
  }
  const context: AuthContextType = {
    loading,
    login,
    logout,
    registerAccount,
    registerChatAccount,
    currentUser,
     chatUser,
  };

  return <AuthContext.Provider value={context}>{children}</AuthContext.Provider>;
}

interface AuthContextType {
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  registerAccount: (registration: AccountRegistration) => Promise<void>;
  registerChatAccount: (chatUser: ChatUser) => Promise<void>;
  loading: boolean;
  currentUser?: Principal;
  chatUser?: ChatUser;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);
