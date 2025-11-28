import type { FlowDefinition } from "../types";
import type { FlowDefinitionPayload } from "../types";

const getFlowDefinitions = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition`);
  return await response.json() as FlowDefinition[];
};

const addNewFlowDefinition = async (body: FlowDefinitionPayload) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  console.log(body);

  return await response.json();
};

const updateFlowDefinition = async (id: string, data: FlowDefinition) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error(`Failed to update flow definition (status ${response.status})`);
  }

  return (await response.json()) as FlowDefinition;
}

const deleteFlowDefinition = async (id: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error(`Failed to delete flow definition (status ${response.status})`);
  }

  return true;
}

export default {
  getFlowDefinitions,
  addNewFlowDefinition,
  updateFlowDefinition,
  deleteFlowDefinition
};