import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getAccount } from '../api/AccountApi'
import { getAllTransactions, createTransaction, deleteTransaction } from '../api/TransactionApi'
import { getLimits, createLimit, deleteLimit } from '../api/Limits'
import { getSummary } from '../api/SummaryApi'
import type { Account } from '../types/account'
import type { Transaction } from '../types/transaction'
import type { Limit } from '../types/limit'
import type { Summary } from '../types/summary'
import './AccountPage.css'
import { exportTransactions } from '../api/AccountApi'



export default function AccountPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const accountId = Number(id)

  const [account, setAccount] = useState<Account | null>(null)
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [limits, setLimits] = useState<Limit[]>([])
  const [summary, setSummary] = useState<Summary | null>(null)
  const [loading, setLoading] = useState(true)

  const [filterFrom, setFilterFrom] = useState('')
  const [filterTo, setFilterTo] = useState('')
  const [filterCategory, setFilterCategory] = useState('')

  const [txAmount, setTxAmount] = useState('')
  const [txType, setTxType] = useState<'INCOME' | 'EXPENSE'>('INCOME')
  const [txCategory, setTxCategory] = useState('')
  const [txDesc, setTxDesc] = useState('')
  const [txDate, setTxDate] = useState(new Date().toISOString().split('T')[0])
  const [txError, setTxError] = useState<string | null>(null)
  const [txWarning, setTxWarning] = useState<string | null>(null)

  const [limitCategory, setLimitCategory] = useState('')
  const [limitAmount, setLimitAmount] = useState('')
  const [limitError, setLimitError] = useState<string | null>(null)

  useEffect(() => {
    loadAll()
  }, [accountId])

  async function loadAll() {
    setLoading(true)
    try {
      const [acc, txs, lims, sum] = await Promise.all([
        getAccount(accountId),
        getAllTransactions(),
        getLimits(accountId),
        getSummary()
      ])
      setAccount(acc)
      setTransactions(txs.filter(t => t.accountId === accountId))
      setLimits(lims)
      setSummary(sum)
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Failed to load data.')
    } finally {
      setLoading(false)
    }
  }

  async function applyFilters() {
    try {
      const txs = await getAllTransactions(
        filterFrom || undefined,
        filterTo || undefined,
        filterCategory || undefined
      )
      setTransactions(txs.filter(t => t.accountId === accountId))
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Failed to filter transactions.')
    }
  }

  async function handleCreateTransaction() {
    if (!txAmount || !txCategory) {
      setTxError('Amount and category are required.')
      return
    }
    setTxError(null)
    setTxWarning(null)
    try {
      const res = await createTransaction({
        amount: parseFloat(txAmount),
        type: txType,
        category: txCategory,
        description: txDesc || undefined,
        transactionDate: txDate,
        accountId
      })
      if (res.warning) setTxWarning(res.warning)
      setTxAmount('')
      setTxCategory('')
      setTxDesc('')
      await loadAll()
    } catch (e) {
      setTxError(e instanceof Error ? e.message : 'Failed to create transaction.')
    }
  }

  async function handleDeleteTransaction(txId: number) {
    if (!confirm('Delete this transaction?')) return
    try {
      await deleteTransaction(txId)
      await loadAll()
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Failed to delete transaction.')
    }
  }

  async function handleCreateLimit() {
    if (!limitCategory || !limitAmount) {
      setLimitError('Category and amount are required.')
      return
    }
    setLimitError(null)
    try {
      await createLimit(accountId, { category: limitCategory, limitAmount: parseFloat(limitAmount) })
      setLimitCategory('')
      setLimitAmount('')
      const lims = await getLimits(accountId)
      setLimits(lims)
    } catch (e) {
      setLimitError(e instanceof Error ? e.message : 'Failed to create limit.')
    }
  }

  async function handleDeleteLimit(category: string) {
    try {
      await deleteLimit(accountId, category)
      setLimits(prev => prev.filter(l => l.category !== category))
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Failed to delete limit.')
    }
  }

  if (loading) return <div className="acp-loading">Loading...</div>
  if (!account) return <div className="acp-loading">Account not found.</div>

  return (
    <div className="acp-root">
      <header className="acp-header">
        <div className="acp-header-inner">
          <button className="acp-back" onClick={() => navigate('/')}>← Back</button>
          <button className="acp-btn acp-btn--sm" onClick={() => exportTransactions(accountId)}>
            ↓ Export CSV
          </button>
          <div className="acp-account-info">
            <h1 className="acp-account-name">{account.name}</h1>
            <span className="acp-account-balance">{(account.balance ?? 0).toFixed(2)} PLN</span>
          </div>
        </div>
      </header>

      <main className="acp-main">
        <div className="acp-layout">

          <div className="acp-left">

            <section className="acp-section">
              <div className="acp-section-title">Add transaction</div>
              <div className="acp-form">
                <div className="acp-form-row">
                  <input className="acp-input" placeholder="Amount" type="number" min="0.01" step="0.01"
                    value={txAmount} onChange={e => setTxAmount(e.target.value)} />
                  <select className="acp-input acp-select" value={txType}
                    onChange={e => setTxType(e.target.value as 'INCOME' | 'EXPENSE')}>
                    <option value="INCOME">Income</option>
                    <option value="EXPENSE">Expense</option>
                  </select>
                </div>
                <input className="acp-input" placeholder="Category" value={txCategory}
                  onChange={e => setTxCategory(e.target.value)} />
                <input className="acp-input" placeholder="Description (optional)" value={txDesc}
                  onChange={e => setTxDesc(e.target.value)} />
                <input className="acp-input" type="date" value={txDate}
                  onChange={e => setTxDate(e.target.value)} />
                {txError && <div className="acp-error">{txError}</div>}
                {txWarning && <div className="acp-warning">⚠ {txWarning}</div>}
                <button className="acp-btn" onClick={handleCreateTransaction}>+ Add</button>
              </div>
            </section>

            <section className="acp-section">
              <div className="acp-section-title">Budget limits</div>
              <div className="acp-form">
                <input className="acp-input" placeholder="Category" value={limitCategory}
                  onChange={e => setLimitCategory(e.target.value)} />
                <input className="acp-input" placeholder="Limit amount" type="number" min="0.01"
                  value={limitAmount} onChange={e => setLimitAmount(e.target.value)} />
                {limitError && <div className="acp-error">{limitError}</div>}
                <button className="acp-btn" onClick={handleCreateLimit}>+ Set limit</button>
              </div>
              {limits.length > 0 && (
                <div className="acp-limits-list">
                  {limits.map(l => (
                    <div key={l.id} className="acp-limit-row">
                      <span className="acp-limit-cat">{l.category}</span>
                      <span className="acp-limit-amt">{(l.limitAmount ?? 0).toFixed(2)} PLN</span>
                      <button className="acp-limit-del" onClick={() => handleDeleteLimit(l.category)}>✕</button>
                    </div>
                  ))}
                </div>
              )}
            </section>

            {summary && (
              <section className="acp-section acp-summary">
                <div className="acp-section-title">Summary</div>
                <div className="acp-summary-grid">
                  <div className="acp-summary-card acp-summary-card--income">
                    <div className="acp-summary-label">Total income</div>
                    <div className="acp-summary-value">+{(summary.totalIncome ?? 0).toFixed(2)}</div>
                  </div>
                  <div className="acp-summary-card acp-summary-card--expense">
                    <div className="acp-summary-label">Total expenses</div>
                    <div className="acp-summary-value">−{(summary.totalExpenses ?? 0).toFixed(2)}</div>
                  </div>
                </div>
                {summary.expensesByCategory && Object.keys(summary.expensesByCategory).length > 0 && (
                  <div className="acp-summary-cats">
                    <div className="acp-summary-cats-title">By category</div>
                    {Object.entries(summary.expensesByCategory).map(([cat, amt]) => (
                      <div key={cat} className="acp-summary-cat-row">
                        <span>{cat}</span>
                        <span className="acp-summary-cat-amt">{Number(amt).toFixed(2)} PLN</span>
                      </div>
                    ))}
                  </div>
                )}
              </section>
            )}
          </div>

          <div className="acp-right">
            <section className="acp-section acp-section--full">
              <div className="acp-section-title">Transactions</div>

              <div className="acp-filters">
                <input className="acp-input acp-input--sm" type="date" placeholder="From"
                  value={filterFrom} onChange={e => setFilterFrom(e.target.value)} />
                <input className="acp-input acp-input--sm" type="date" placeholder="To"
                  value={filterTo} onChange={e => setFilterTo(e.target.value)} />
                <input className="acp-input acp-input--sm" placeholder="Category"
                  value={filterCategory} onChange={e => setFilterCategory(e.target.value)} />
                <button className="acp-btn acp-btn--sm" onClick={applyFilters}>Filter</button>
                <button className="acp-btn acp-btn--sm acp-btn--ghost" onClick={loadAll}>Reset</button>
              </div>

              {transactions.length === 0 ? (
                <div className="acp-empty">No transactions found.</div>
              ) : (
                <div className="acp-tx-list">
                  {transactions.map(tx => (
                    <div key={tx.id} className={`acp-tx-row acp-tx-row--${tx.type.toLowerCase()}`}>
                      <div className="acp-tx-left">
                        <span className="acp-tx-cat">{tx.category}</span>
                        {tx.description && <span className="acp-tx-desc">{tx.description}</span>}
                        <span className="acp-tx-date">{tx.transactionDate}</span>
                      </div>
                      <div className="acp-tx-right">
                        <span className="acp-tx-amount">
                          {tx.type === 'INCOME' ? '+' : '−'}{Number(tx.amount).toFixed(2)}
                        </span>
                        <span className={`acp-tx-badge acp-tx-badge--${tx.type.toLowerCase()}`}>{tx.type}</span>
                        <button className="acp-tx-del" onClick={() => handleDeleteTransaction(tx.id)}>✕</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </section>
          </div>

        </div>
      </main>
    </div>
  )
}
