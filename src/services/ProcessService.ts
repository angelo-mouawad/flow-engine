import type { Process } from "../types";

const getProcess = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/process`)
  const data = await response.json() as Process[]
  console.log("Fetched processes:", data);
  return data
}

const getProcessTypes = async () => {
  const processes = await getProcess()
  const types = [...new Set(processes.map(p => p.processType))]
  return types
}

export default {
  getProcess,
  getProcessTypes,
}