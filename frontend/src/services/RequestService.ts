import type { RequestSubmission } from "../types";
import { apiRequest } from "./api";

const getRequests = () => apiRequest<RequestSubmission[]>("/request/pending", { asUser: true });

const getRequestById = (id: string) =>
  apiRequest<RequestSubmission>(`/request/pending/${encodeURIComponent(id)}`, { asUser: true });

const approveRequest = (id: string) =>
  apiRequest<RequestSubmission>(`/request/approve/${encodeURIComponent(id)}`, { method: "PUT", asUser: true });

const declineRequest = (id: string) =>
  apiRequest<RequestSubmission>(`/request/decline/${encodeURIComponent(id)}`, { method: "PUT", asUser: true });

export default {
  getRequests,
  getRequestById,
  approveRequest,
  declineRequest,
};
