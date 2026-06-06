import type {Account} from "../types/account.ts";

const API_URL = '/api/accounts';
async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        const message = body?.error || `Request failed: ${response.status}`
        throw new Error(message)
    }
    return response.json()
}

export async function getAllAccounts(): Promise<Account[]> {
    const response = await fetch(API_URL)
    return handleResponse(response)
}

export async function getAccount(id: number): Promise<Account> {
    const response = await fetch(`${API_URL}/${id}`)
    return handleResponse(response)
}

export async function createAccount(data: { balance: number; name: string }): Promise<Account> {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    return handleResponse(response)
}

export async function deleteAccount(id: number): Promise<void> {
    const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" })
    if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.error || `Failed to delete account: ${response.status}`)
    }
}

export async function exportTransactions(accountId: number): Promise<void> {
    const response = await fetch(`${API_URL}/${accountId}/transactions/export`)
    if (!response.ok) throw new Error('Export failed.')
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `transactions_${accountId}.csv`
    a.click()
    URL.revokeObjectURL(url)
}