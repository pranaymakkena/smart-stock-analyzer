import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { TrendingUp, Eye, EyeOff } from 'lucide-react'
import toast from 'react-hot-toast'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate  = useNavigate()
  const [form, setForm]       = useState({ email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [showPwd, setShowPwd] = useState(false)

  const handleSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      await login(form.email, form.password)
      toast.success('Welcome back!')
      navigate('/dashboard')
    } catch {
      // error toast handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  const fillDemo = (email, password) => setForm({ email, password })

  return (
    <div className="min-h-screen flex items-center justify-center p-4" style={{ background: '#0d0f14' }}>
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <div
            className="inline-flex items-center justify-center w-14 h-14 rounded-2xl mb-4"
            style={{ background: 'linear-gradient(135deg, #7c3aed, #0ea5e9)' }}
          >
            <TrendingUp size={26} className="text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Smart Stock Analyzer</h1>
          <p className="mt-1 text-sm" style={{ color: '#8892a4' }}>Sign in to your account</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm text-slate-400 mb-1.5">Email</label>
              <input
                type="email"
                className="input"
                placeholder="you@example.com"
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>
            <div>
              <label className="block text-sm text-slate-400 mb-1.5">Password</label>
              <div className="relative">
                <input
                  type={showPwd ? 'text' : 'password'}
                  className="input pr-10"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={e => setForm({ ...form, password: e.target.value })}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPwd(!showPwd)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200"
                >
                  {showPwd ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>
            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>

          {/* Demo accounts */}
          <div className="mt-5 pt-5" style={{ borderTop: '1px solid #1f2433' }}>
            <p className="text-xs mb-3 text-center" style={{ color: '#3d4460' }}>Demo Accounts</p>
            <div className="grid grid-cols-3 gap-2">
              {[
                { label: 'Admin',    email: 'admin@stockanalyzer.com',    pwd: 'admin123' },
                { label: 'Investor', email: 'investor@stockanalyzer.com', pwd: 'investor123' },
                { label: 'Analyst',  email: 'analyst@stockanalyzer.com',  pwd: 'analyst123' },
              ].map(d => (
                <button
                  key={d.label}
                  onClick={() => fillDemo(d.email, d.pwd)}
                  className="text-xs py-2 rounded-lg transition-colors"
                  style={{ background: '#1a1d27', color: '#8892a4', border: '1px solid #1f2433' }}
                  onMouseEnter={e => e.currentTarget.style.color = '#e2e8f0'}
                  onMouseLeave={e => e.currentTarget.style.color = '#8892a4'}
                >
                  {d.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        <p className="text-center text-sm mt-4" style={{ color: '#8892a4' }}>
          Don't have an account?{' '}
          <Link to="/register" className="font-medium" style={{ color: '#a78bfa' }}>Register</Link>
        </p>
      </div>
    </div>
  )
}
