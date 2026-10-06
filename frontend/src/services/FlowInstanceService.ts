import type { FlowInstance } from "../types";
import { apiRequest } from "./api";

const getFlowInstances = () => apiRequest<FlowInstance[]>("/flowInstance");

const getFlowInstanceById = (id: string) =>
  apiRequest<FlowInstance>(`/flowInstance/${encodeURIComponent(id)}`);

const deleteFlowInstance = async (id: string) => {
  await apiRequest<void>(`/flowInstance/${encodeURIComponent(id)}`, { method: "DELETE" });
  return true;
};

export default {
  getFlowInstances,
  getFlowInstanceById,
  deleteFlowInstance,
};
