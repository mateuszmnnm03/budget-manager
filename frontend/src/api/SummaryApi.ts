import type {Summary} from "../types/summary.ts";

const API_URL = '/api/summary';

export async function getSummary(): Promise<Summary> {
    const response = await fetch(API_URL)
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.error || `Failed to fetch summary: ${response.status}`)
    }
    return response.json()
}