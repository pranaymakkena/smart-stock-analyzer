import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { TrendingUp } from 'lucide-react'
import toast from 'react-hot-toast'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', role: 'INVESTOR'
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      await register(form)
      toast.success('Account created! Welcome aboard.')
      navigate('/dashboard')
    } catch {
      // handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4" style={{ background: '#0d0f14' }}>
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div
            className="inline-flex items-center justify-center w-14 h-14 rounded-2xl mb-4"
            style={{ background: 'linear-gradient(135deg, #7c3aed, #0ea5e9)' }}
          >
            <TrendingUp size={26} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Create Account</h1>
          <p className="mt-1 text-sm" style={{ color: '#8892a4' }}>Start your investment journey</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm text-slate-400 mb-1.5">First Name</label>
                <input className="input" placeholder="John" value={form.firstName}
                  onChange={e => setForm({ ...form, firstName: e.target.value })} required />
              </div>
              <div>
                <label className="block text-sm text-slate-400 mb-1.5">Last Name</label>
                <input className="input" placeholder="Doe" value={form.lastName}
                  onChange={e => setForm({ ...form, lastName: e.target.value })} required />
              </div>
            </div>
            <div>
              <label className="block text-sm text-slate-400 mb-1.5">Email</label>
              <input type="email" className="input" placeholder="you@example.com" value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })} required />
            </div>
            <div>
              <label className="block text-sm text-slate-400 mb-1.5">Password</label>
              <input type="password" className="input" placeholder="Min 6 characters" value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })} required minLength={6} />
            </div>
            <div>
              <label className="block text-sm text-slate-400 mb-1.5">Role</label>
              <select className="input" value={form.role}
                onChange={e => setForm({ ...form, role: e.target.value })}>
                <option value="INVESTOR">Investor</option>
                <option value="ANALYST">Analyst</option>
              </select>
            </div>
            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>
        </div>

        <p className="text-center text-sm mt-4" style={{ color: '#8892a4' }}>
          Already have an account?{' '}
          <Link to="/login" className="font-medium" style={{ color: '#a78bfa' }}>Sign in</Link>
        </p>
      </div>
    </div>
  )
}
