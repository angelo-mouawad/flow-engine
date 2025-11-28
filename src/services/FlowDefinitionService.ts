import type { FlowDefinition } from "../types";
import type { FlowDefinitionPayload } from "../types";

const getFlowDefinitions = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition`);
  return await response.json() as FlowDefinition[];
};

const getFlowDefinitionById = async (id: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition/${id}`);
  if (!response.ok) {
    throw new Error("Failed to load flow definition");
  }
  return await response.json() as FlowDefinition;
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

export default {
  getFlowDefinitions,
  getFlowDefinitionById,
  addNewFlowDefinition,
};
