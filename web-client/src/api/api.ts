import axios from 'axios';
import { getToken } from './idp';

export default function api(baseUrl: string) {
  const http = axios.create({
    baseURL: baseUrl,
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
  });

  http.interceptors.response.use(
    response => response.data,
    error => Promise.reject(error)
  );

  function get<R>(path: string, params?: unknown): Promise<R> {
    return http.get<never, R>(path, params ? { params } : undefined);
  }

  function getSecure<R>(path: string, params?: unknown): Promise<R> {
    return http.get<never, R>(path, { ...withAuthorization(), params });
  }

  function post<B, R>(path: string, body: B): Promise<R> {
    return http.post<B, R>(path, body);
  }

  function postSecure<B, R>(path: string, body: B): Promise<R> {
    return http.post<B, R>(path, body, withAuthorization());
  }

  function putSecure<B, R>(path: string, body?: B): Promise<R> {
    return http.put<B, R>(path, body, withAuthorization());
  }

  function deleteSecure(path: string): Promise<void> {
    return http.delete(path, withAuthorization());
  }

  function withAuthorization() {
    return {
      headers: {
        Authorization: `Bearer ${getToken()}`,
      },
    };
  }

  return {
    get,
    getSecure,
    post,
    postSecure,
    putSecure,
    deleteSecure,
  };
}
