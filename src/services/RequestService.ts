import { useUserStore } from "../stores/userStore";
import type { RequestSubmission } from "../types";

const getUserEmail = () => {
  const storedUser = sessionStorage.getItem("currentUser");
  if (storedUser) {
    const parsed = JSON.parse(storedUser);
    return parsed.email ?? "adam@glackit.be";
  }

  const userStore = useUserStore();
  return userStore.currentUser?.email ?? "adam@glackit.be";
};

const getRequests = async () => {
  const email = getUserEmail();
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/pending`, {
    method: 'GET',
    headers: {
      "UserEmail": email.replace('.', '_')
    }
  })

  return await response.json() as RequestSubmission[];
};

const getRequestById = async (id: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/pending/${id}`);
  if (!response.ok) {
    throw new Error("Failed to load request");
  }
  return await response.json() as RequestSubmission;
};

const approveRequest = async (requestId: string) => {
  const email = getUserEmail();
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/approve/${requestId}`, {
    method: 'PUT',
    headers: {
      "UserEmail": email.replace('.', '_')
    }
  })
  return response.ok
}

const declineRequest = async (requestId: string) => {
  const email = getUserEmail();
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/decline/${requestId}`, {
    method: 'PUT',
    headers: {
      "UserEmail": email.replace('.', '_')
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
