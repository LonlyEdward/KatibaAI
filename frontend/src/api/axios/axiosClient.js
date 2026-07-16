import axios from "axios";

import requestInterceptor from "../interceptors/requestInterceptor";
import responseInterceptor from "../interceptors/responseInterceptor";

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL + "/api",
  withCredentials: true,

  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

axiosClient.interceptors.request.use(
    requestInterceptor,
    (error) => Promise.reject(error)
);

axiosClient.interceptors.response.use(
    (response) => response,
    responseInterceptor
);

export default axiosClient;