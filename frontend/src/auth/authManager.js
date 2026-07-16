let accessToken = null;
let refreshToken = null;


export const authManager = {

    getAccessToken() {

        return accessToken;

    },


    setAccessToken(token) {

        accessToken = token;

    },


    getRefreshToken() {

        return refreshToken;

    },


    setRefreshToken(token) {

        refreshToken = token;

    },


    clearAccessToken() {

        accessToken = null;

        refreshToken = null;

    }

};