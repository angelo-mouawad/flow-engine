import type { FlowInstance } from "../types";

const getFlowInstances = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowInstance`);
  return await response.json() as FlowInstance[];
};

const getFlowInstanceById = async (id: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowInstance/${id}`);
  if (!response.ok) {
    throw new Error("Failed to load flow instance");
  }
  return await response.json() as FlowInstance;
};

const deleteFlowInstance = async (id: string): Promise<boolean> => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/flowInstance/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Failed to delete flow instance");
  }

  return true;
}

export default {
  getFlowInstances,
  getFlowInstanceById,
  deleteFlowInstance,
};
