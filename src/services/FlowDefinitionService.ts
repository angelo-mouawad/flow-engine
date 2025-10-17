import type { FlowDefinition } from "../types";

const getFlowDefinitions = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowDefinition`);
  return await response.json() as FlowDefinition[]
}

export default {
  getFlowDefinitions,
}
