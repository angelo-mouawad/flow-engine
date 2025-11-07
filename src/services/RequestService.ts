import type { RequestSubmission } from "../types";

const getRequests = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/pending`)
  const data = await response.json() as RequestSubmission[]
  return data
}

const approveRequest = async (requestId: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/approve/${requestId}`, {
    method: 'POST',
  })
  return response.ok
}

const declineRequest = async (requestId: string) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/request/decline/${requestId}`, {
    method: 'POST',
  })
  return response.ok
}

export default {
  getRequests,
  approveRequest,  
  declineRequest
}
