export interface ChatRegistration {
  nickName: string;
  email?: string;
  statusMessage?: string;
  picture?: string;
}

export interface ChatUser {
  id: string;
  nickName: string;
  statusMessage?: string;
  picture?: string;
}

export interface UserUpdate {
  type: UpdateType;
  user: ChatUser;
  changedAt: string;
}

export enum UpdateType {
  CHANGED,
  DELETED,
}
