import axiosClient from "../axios/axiosClient";
import { authManager } from "../../auth/authManager";
import authService from "../../services/auth/authService";

let isRefreshing = false;
let failedQueue = [];

function processQueue(error, token = null) {
    failedQueue.forEach((promise) => {
        if (error) {
            promise.reject(error);
        } else {
            promise.resolve(token);
        }
    });

    failedQueue = [];
}

async function responseInterceptor(error) {
    const originalRequest = error.config;

    if (!error.response) {
        return Promise.reject(error);
    }

    if (
        error.response.status !== 401 ||
        originalRequest._retry
    ) {
        return Promise.reject(error);
    }

    originalRequest._retry = true;

    if (isRefreshing) {
        return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
        }).then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return axiosClient(originalRequest);
        });
    }

    isRefreshing = true;

    try {

        const response = await authService.refresh({
            refreshToken: null,
        });

        const accessToken = response.token;
        authManager.setAccessToken(accessToken);

        processQueue(null, accessToken);

        originalRequest.headers.Authorization =
            `Bearer ${accessToken}`;

        return axiosClient(originalRequest);

    } catch (refreshError) {

        processQueue(refreshError);

        authManager.clearAccessToken();

        return Promise.reject(refreshError);

    } finally {

        isRefreshing = false;

    }
}

export default responseInterceptor;