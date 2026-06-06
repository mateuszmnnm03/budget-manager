export interface Transaction {
        id: number;
        amount: number;
        type: 'INCOME' | 'EXPENSE';
        category: string;
        description?: string;
        transactionDate: string;
        accountId: number;
        warning: string;
}