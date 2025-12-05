import type { FlowDefinition } from "../types";
import type { FlowDefinitionPayload } from "../types";

const getUserHeader = (): string => {
  const user = sessionStorage.getItem("user") ?? "adam@glackit.be";
  return user.replace(".", "_");
};

const getFlowDefinitions = async () => {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/flowDefinition`,
    {
      headers: {
        UserEmail: getUserHeader(),
      },
    }
  );
  return (await response.json()) as FlowDefinition[];
};

const getFlowDefinitionById = async (id: string) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/flowDefinition/${id}`,
    {
      headers: {
        UserEmail: getUserHeader(),
      },
    }
  );
  if (!response.ok) {
    throw new Error("Failed to load flow definition");
  }
  return (await response.json()) as FlowDefinition;
};

const addNewFlowDefinition = async (body: FlowDefinitionPayload) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/flowDefinition`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        UserEmail: getUserHeader(),
      },
      body: JSON.stringify(body),
    }
  );

  return await response.json();
};

const updateFlowDefinition = async (id: string, data: FlowDefinition) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/flowDefinition/${id}`,
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        UserEmail: getUserHeader(),
      },
      body: JSON.stringify(data),
    }
  );

  if (!response.ok) {
    throw new Error(
      `Failed to update flow definition (status ${response.status})`
    );
  }

  return (await response.json()) as FlowDefinition;
};

const deleteFlowDefinition = async (id: string) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/flowDefinition/${id}`,
    {
      method: "DELETE",
      headers: {
        UserEmail: getUserHeader(),
      },
    }
  );

  if (!response.ok) {
    throw new Error(
      `Failed to delete flow definition (status ${response.status})`
    );
  }

  return true;
};

export default {
  getFlowDefinitions,
  getFlowDefinitionById,
  addNewFlowDefinition,
  updateFlowDefinition,
  deleteFlowDefinition,
};
