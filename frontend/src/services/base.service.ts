import { useAuth } from "@/application/authStore";
import { ApiError } from "@/types/api-error";
import axios, { AxiosInstance } from "axios";

const SERVER_URL = import.meta.env.VITE_APP_BASE_URL + "/api";

export default class BaseService {
  protected readonly instance: AxiosInstance;
  
  constructor() {
    this.instance = axios.create({
      baseURL: SERVER_URL,
      timeout: 12000,
    });
    this.initAxios();
  }

  protected initAxios(): void {
    const auth = useAuth();

    this.instance.interceptors.request.use(async (config) => {
      const accessToken = await auth.getAccessToken();
      if (accessToken) {
        config.headers.Authorization = "Bearer " + accessToken;
      }
      return config;
    });
    
    this.instance.interceptors.response.use(response => response, error => {
      const message = error.status >= 500 ? "A server error occurred. Try again or report an issue if the problem keeps reoccurring." : error.message;
      return Promise.reject({ statusCode: error.status, message } satisfies ApiError)
    });
  }
}