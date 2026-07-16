import axiosClient from "../../api/axios/axiosClient";

const AUTH_URL = "/auth";

const authService = {
    async register(registerData) {
        const response = await axiosClient.post(
            `${AUTH_URL}/register`,
            registerData
        );

        return response.data;
    },

    async login(loginData) {
        const response = await axiosClient.post(
            `${AUTH_URL}/login`,
            loginData
        );

        return response.data;
    },

    async refresh(data) {

        const response = await axiosClient.post(

            "/auth/refresh",

            data

        );

        return response.data;

    },

    async logout(refreshData) {
        await axiosClient.post(
            `${AUTH_URL}/logout`,
            refreshData
        );
    },
};

export default authService;