import { BrowserRouter, Routes, Route } from 'react-router-dom'
import AccountsPage from './pages/AccountsPage'
import AccountPage from './pages/AccountPage'

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AccountsPage />} />
          <Route path="/accounts/:id" element={<AccountPage />} />
        </Routes>
      </BrowserRouter>
  )
}

export default App