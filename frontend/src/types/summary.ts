export interface Summary {
    totalIncome: number
    totalExpenses: number
    expensesByCategory: Record<string, number>
}