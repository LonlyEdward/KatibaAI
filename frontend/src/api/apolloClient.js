import { ApolloClient, InMemoryCache, HttpLink, from } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { onError } from '@apollo/client/link/error';
import axios from 'axios';
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from './tokenStore';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const httpLink = new HttpLink({
  uri: `${API_URL}/graphql`,
});

const authLink = setContext((_, { headers }) => {
  const token = getAccessToken();
  return {
    headers: {
      ...headers,
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  };
});

let refreshPromise = null;

async function refreshAccessToken() {
  if (!refreshPromise) {
    refreshPromise = axios
      .post(`${API_URL}/api/auth/refresh`, { refreshToken: getRefreshToken() })
      .then((res) => {
        setTokens(res.data.token, res.data.refreshToken);
        return res.data.token;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }
  return refreshPromise;
}

const errorLink = onError(({ graphQLErrors, operation, forward }) => {
  if (graphQLErrors) {
    for (const err of graphQLErrors) {
      const isUnauthenticated =
        err.extensions?.classification === 'UNAUTHORIZED' ||
        err.extensions?.classification === 'UNAUTHENTICATED' ||
        err.message?.toLowerCase().includes('unauthorized');

      if (isUnauthenticated && getRefreshToken()) {
        return new Promise((resolve, reject) => {
          refreshAccessToken()
            .then((newToken) => {
              operation.setContext(({ headers = {} }) => ({
                headers: {
                  ...headers,
                  Authorization: `Bearer ${newToken}`,
                },
              }));
              resolve(forward(operation));
            })
            .catch((refreshError) => {
              clearTokens();
              window.location.href = '/login';
              reject(refreshError);
            });
        });
      }
    }
  }
});

const apolloClient = new ApolloClient({
  link: from([errorLink, authLink, httpLink]),
  cache: new InMemoryCache(),
});

export default apolloClient;
