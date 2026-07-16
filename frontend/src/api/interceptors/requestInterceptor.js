import { authManager } from "../../auth/authManager";

function requestInterceptor(config) {
    const token = authManager.getAccessToken();

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}

export default requestInterceptor;