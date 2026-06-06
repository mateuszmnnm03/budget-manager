import type {Transaction} from "../types/transaction.ts";

const API_URL = '/api/transactions';

async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        const message = body?.error || `Request failed: ${response.status}`
        throw new Error(message)
    }
    return response.json()
}

export interface CreateTransactionRequest {
    amount: number;
    type: 'INCOME' | 'EXPENSE';
    category: string;
    description?: string;
    transactionDate: string;
    accountId: number;
}

export async function getAllTransactions(
    from?: string,
    to?: string,
    category?: string
): Promise<Transaction[]> {
    const params = new URLSearchParams();
    if (from) params.append("from", from);
    if (to) params.append("to", to);
    if (category) params.append("category", category);

    const url = params.size > 0 ? `${API_URL}?${params}` : API_URL;
    const response = await fetch(url)
    return handleResponse(response)
}

export async function createTransaction(data: CreateTransactionRequest): Promise<Transaction> {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    return handleResponse(response)
}

export async function deleteTransaction(id: number): Promise<void> {
    const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" })
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.error || `Failed to delete transaction: ${response.status}`)
    }
}