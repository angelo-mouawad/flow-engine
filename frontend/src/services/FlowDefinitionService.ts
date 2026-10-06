import type { FlowDefinition, FlowDefinitionPayload } from "../types";
import { apiRequest } from "./api";

const getFlowDefinitions = () => apiRequest<FlowDefinition[]>("/flowDefinition");

const getFlowDefinitionById = (id: string) =>
  apiRequest<FlowDefinition>(`/flowDefinition/${encodeURIComponent(id)}`);

const addNewFlowDefinition = (body: FlowDefinitionPayload) =>
  apiRequest<FlowDefinition>("/flowDefinition", { method: "POST", body });

const updateFlowDefinition = (id: string, data: FlowDefinitionPayload) =>
  apiRequest<FlowDefinition>(`/flowDefinition/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: {
      title: data.title,
      description: data.description,
      triggerableBy: data.triggerableBy,
      processes: data.processes,
    },
  });

const deleteFlowDefinition = async (id: string) => {
  await apiRequest<void>(`/flowDefinition/${encodeURIComponent(id)}`, { method: "DELETE" });
  return true;
};

export default {
  getFlowDefinitions,
  getFlowDefinitionById,
  addNewFlowDefinition,
  updateFlowDefinition,
  deleteFlowDefinition,
};
