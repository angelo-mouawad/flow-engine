import type { FlowInstance } from "../types";

const getFlowInstances = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowInstance`)
  const data = await response.json() as FlowInstance[]
  return data
}

export default {
  getFlowInstances,
}
