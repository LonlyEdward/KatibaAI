// Simple in-memory token store shared across axios, apollo, and React context.
// NOTE: tokens live only in memory — refreshing the browser tab clears them
// and the user has to log in again. This was a deliberate tradeoff (chosen
// over localStorage) for simplicity/security over persistence.

let accessToken = null;
let refreshToken = null;
const listeners = new Set();

export function getAccessToken() {
  return accessToken;
}

export function getRefreshToken() {
  return refreshToken;
}

export function setTokens(newAccessToken, newRefreshToken) {
  accessToken = newAccessToken;
  refreshToken = newRefreshToken;
  listeners.forEach((listener) => listener());
}

export function clearTokens() {
  accessToken = null;
  refreshToken = null;
  listeners.forEach((listener) => listener());
}

export function subscribe(listener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}
