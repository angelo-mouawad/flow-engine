import { useUserStore } from "../stores/userStore";

const BASE_URL = (import.meta.env.VITE_API_URL ?? "http://localhost:8080").replace(/\/+$/, "");

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

type RequestOptions = {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  asUser?: boolean;
};

const readErrorMessage = async (response: Response): Promise<string> => {
  try {
    const body = await response.json();
    if (body && typeof body.error === "string" && body.error.trim()) {
      return body.error;
    }
  } catch {
    return `Request failed with status ${response.status}`;
  }
  return `Request failed with status ${response.status}`;
};

export const currentUserEmail = (): string | null => useUserStore().currentUser?.email ?? null;

export const apiRequest = async <T>(path: string, options: RequestOptions = {}): Promise<T> => {
  const headers: Record<string, string> = {};

  if (options.body !== undefined) {
    headers["Content-Type"] = "application/json";
  }

  if (options.asUser) {
    const email = currentUserEmail();
    if (!email) {
      throw new ApiError(401, "Select a user first");
    }
    headers.UserEmail = email;
  }

  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method: options.method ?? "GET",
      headers,
      body: options.body === undefined ? undefined : JSON.stringify(options.body),
    });
  } catch {
    throw new ApiError(0, "Could not reach the server. Is the backend running?");
  }

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
};

export const errorMessage = (error: unknown, fallback: string): string =>
  error instanceof Error && error.message ? error.message : fallback;

export const apiBaseUrl = BASE_URL;
