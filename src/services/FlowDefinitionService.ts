import type { FlowDefinition } from "../types";

const getFlowDefinitions = async () => {
  const response = await fetch("http://localhost:8080/flowDefinition", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    }
  })
  return await response.json() as FlowDefinition[]
}

export default {
  getFlowDefinitions,
}