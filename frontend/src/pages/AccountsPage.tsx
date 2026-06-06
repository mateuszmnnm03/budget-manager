import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAllAccounts, createAccount, deleteAccount } from '../api/AccountApi'
import type { Account } from '../types/account'
import './AccountsPage.css'

export default function AccountsPage() {
  const navigate = useNavigate()
  const [accounts, setAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [newName, setNewName] = useState('')
  const [newBalance, setNewBalance] = useState('')
  const [creating, setCreating] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  useEffect(() => {
    fetchAccounts()
  }, [])

  async function fetchAccounts() {
    try {
      setLoading(true)
      const data = await getAllAccounts()
      setAccounts(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load accounts.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate() {
    if (!newName.trim()) {
      setFormError('Account name is required.')
      return
    }
    setCreating(true)
    setFormError(null)
    try {
      await createAccount({ name: newName.trim(), balance: newBalance ? parseFloat(newBalance) : 0 })
      setNewName('')
      setNewBalance('')
      await fetchAccounts()
    } catch (e) {
      setFormError(e instanceof Error ? e.message : 'Failed to create account.')
    } finally {
      setCreating(false)
    }
  }

  async function handleDelete(id: number, e: React.MouseEvent) {
    e.stopPropagation()
    if (!confirm('Delete this account?')) return
    try {
      await deleteAccount(id)
      setAccounts(prev => prev.filter(a => a.id !== id))
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Cannot delete account.')
    }
  }

  return (
    <div className="ap-root">
      <header className="ap-header">
        <div className="ap-header-inner">
          <span className="ap-logo">₿</span>
          <h1 className="ap-title">Budget Manager</h1>
        </div>
      </header>

      <main className="ap-main">
        <div className="ap-section-label">Your accounts</div>

        {loading && <div className="ap-state">Loading...</div>}
        {error && <div className="ap-state ap-state--error">{error}</div>}

        <div className="ap-grid">
          {accounts.map(account => (
            <div
              key={account.id}
              className="ap-card"
              onClick={() => navigate(`/accounts/${account.id}`)}
            >
              <div className="ap-card-top">
                <span className="ap-card-name">{account.name}</span>
                <button
                  className="ap-card-delete"
                  onClick={e => handleDelete(account.id, e)}
                  title="Delete account"
                >
                  ✕
                </button>
              </div>
              <div className="ap-card-balance">
                {Number(account.balance).toFixed(2)}
                <span className="ap-card-currency">PLN</span>
              </div>
              <div className="ap-card-footer">
                <span className="ap-card-hint">Click to view transactions →</span>
              </div>
            </div>
          ))}

          <div className="ap-card ap-card--new">
            <div className="ap-new-label">New account</div>
            <input
              className="ap-input"
              placeholder="Account name"
              value={newName}
              onChange={e => setNewName(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleCreate()}
            />
            <input
              className="ap-input"
              placeholder="Initial balance (optional)"
              type="number"
              min="0"
              value={newBalance}
              onChange={e => setNewBalance(e.target.value)}
            />
            {formError && <div className="ap-form-error">{formError}</div>}
            <button
              className="ap-btn-create"
              onClick={handleCreate}
              disabled={creating}
            >
              {creating ? 'Creating...' : '+ Add account'}
            </button>
          </div>
        </div>
      </main>
    </div>
  )
}
