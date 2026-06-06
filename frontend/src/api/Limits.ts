import type {Limit} from "../types/limit.ts";

const API_URL = '/api/accounts';

async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        const message = body?.error || `Request failed: ${response.status}`
        throw new Error(message)
    }
    return response.json()
}

export interface CreateLimitRequest {
    category: string;
    limitAmount: number;
}

export async function getLimits(accountId: number): Promise<Limit[]> {
    const response = await fetch(`${API_URL}/${accountId}/limits`)
    return handleResponse(response)
}

export async function createLimit(accountId: number, data: CreateLimitRequest): Promise<Limit> {
    const response = await fetch(`${API_URL}/${accountId}/limits`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    return handleResponse(response)
}

export async function deleteLimit(accountId: number, category: string): Promise<void> {
    const response = await fetch(`${API_URL}/${accountId}/limits/${category}`, { method: "DELETE" })
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.error || `Failed to delete limit: ${response.status}`)
    }
}