import { idp, Principal, TokenResponse } from '../api/idp';
import React, {
  createContext,
  JSX,
  PropsWithChildren,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { accountService, AccountRegistration, ChatAccountRegistration } from '../api/account';
import { ChatRegistration, ChatUser, userService } from '../api/user';

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
      refreshInterval = setTimeout(
        () => {
          idp.refreshToken().then(setToken).catch(clearToken);
        },
        (token.expires_in - 5) * 1_000
      );
    }
    return () => clearTimeout(refreshInterval);
  }, [token]);

  const currentUser = useMemo(() => {
    if (token) {
      return idp.extractPrincipal(token);
    }
  }, [token]);

  async function login(username: string, password: string): Promise<void> {
    setLoading(true);
    await idp.login(username, password).then(setToken).catch(clearToken).finally(disableLoading);
  }
  async function logout(): Promise<void> {
    setLoading(true);
    await idp.logout().then(clearToken).catch(clearToken).finally(disableLoading);
  }

  async function registerAccount(registration: AccountRegistration): Promise<void> {
    setLoading(true);
    await accountService.registerAccount(registration).finally(disableLoading);
  }

  async function registerChatAccount(registration: ChatRegistration): Promise<void> {
    setLoading(true);
    await userService
      .register(registration)
      .then(user => {
        setChatUser(user);
        return user;
      })
      .then(async chatUser => {
        if (token) {
          await accountService.updateChatUserId(chatUser.id);
        } else {
          const chatRegistration: ChatAccountRegistration = {
            nickName: chatUser.nickName,
            chatUserId: chatUser.id,
          };
          await accountService
            .registerChatAccount(chatRegistration)
            .then(idp.toTokenResponse)
            .then(setToken);
        }
      })
      .finally(disableLoading);
  }

  async function exitChat(): Promise<void> {
    setLoading(true);
    await userService
      .removeUser()
      .then(async () => {
        await accountService.removeChatAccount();
        if (currentUser && currentUser.isTemporary) {
          await logout();
        }
        setChatUser(undefined);
      })
      .finally(disableLoading);
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
    exitChat,
    currentUser,
    chatUser,
  };

  return <AuthContext.Provider value={context}>{children}</AuthContext.Provider>;
}

interface AuthContextType {
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  registerAccount: (registration: AccountRegistration) => Promise<void>;
  registerChatAccount: (registration: ChatRegistration) => Promise<void>;
  exitChat: () => Promise<void>;
  loading: boolean;
  currentUser?: Principal;
  chatUser?: ChatUser;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);
