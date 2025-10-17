import type { FlowInstance } from "../types";

const getFlowInstances = async () => {
  const response = await fetch("/instances")
  const data = await response.json() as FlowInstance[]
  return data
}

export default {
  getFlowInstances,
}