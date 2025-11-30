import type { RequestSubmission } from "../types";

const getRequests = async () => {
  const user = sessionStorage.getItem("user")??"adam@glackit.be";
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/pending`, {
    method: 'GET',
    headers: {
      "UserEmail": user.replace('.', '_')
    }
  })

  return await response.json() as RequestSubmission[];
};

const getRequestById = async (id: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/${id}`);
  if (!response.ok) {
    throw new Error("Failed to load request");
  }
  return await response.json() as RequestSubmission;
};

const approveRequest = async (requestId: string) => {
  const user = sessionStorage.getItem("user")??"adam@glackit.be";
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/approve/${requestId}`, {
    method: 'PUT',
    headers: {
      "UserEmail": user.replace('.', '_')
    }
  })
  return response.ok
}

const declineRequest = async (requestId: string) => {
  const user = sessionStorage.getItem("user")??"adam@glackit.be";
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/decline/${requestId}`, {
    method: 'PUT',
    headers: {
      "UserEmail": user.replace('.', '_')
    }
  })
  return response.ok
}

export default {
  getRequests,
  getRequestById,
  approveRequest,  
  declineRequest
}
