let accessToken = null;

export const authManager = {
    getAccessToken() {
        return accessToken;
    },

    setAccessToken(token) {
        accessToken = token;
    },

    clearAccessToken() {
        accessToken = null;
    },
};