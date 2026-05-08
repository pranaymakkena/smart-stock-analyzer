import React, { useState } from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import {
  LayoutDashboard, TrendingUp, Briefcase, Star, Brain,
  Bell, BarChart2, Trophy, ArrowLeftRight, LogOut,
  Menu, X, User
} from 'lucide-react'
import clsx from 'clsx'

const navItems = [
  { to: '/dashboard',    icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/market',       icon: BarChart2,        label: 'Market' },
  { to: '/portfolio',    icon: Briefcase,        label: 'Portfolio' },
  { to: '/watchlist',    icon: Star,             label: 'Watchlist' },
  { to: '/predictions',  icon: Brain,            label: 'Predictions' },
  { to: '/alerts',       icon: Bell,             label: 'Alerts' },
  { to: '/transactions', icon: ArrowLeftRight,   label: 'Transactions' },
  { to: '/leaderboard',  icon: Trophy,           label: 'Leaderboard' },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(true)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: '#0d0f14' }}>

      {/* ── Sidebar ─────────────────────────────────────────────────── */}
      <aside
        className={clsx(
          'flex flex-col flex-shrink-0 transition-all duration-300',
          sidebarOpen ? 'w-60' : 'w-16'
        )}
        style={{ background: '#13161e', borderRight: '1px solid #1f2433' }}
      >
        {/* Logo */}
        <div
          className="flex items-center gap-3 px-4 py-5"
          style={{ borderBottom: '1px solid #1f2433' }}
        >
          <div
            className="w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0"
            style={{ background: 'linear-gradient(135deg, #7c3aed, #0ea5e9)' }}
          >
            <TrendingUp size={16} className="text-white" />
          </div>
          {sidebarOpen && (
            <div className="leading-tight">
              <p className="text-sm font-bold text-white tracking-tight">Smart Stock</p>
              <p className="text-[11px] font-medium" style={{ color: '#a78bfa' }}>Analyzer</p>
            </div>
          )}
        </div>

        {/* Nav */}
        <nav className="flex-1 py-3 overflow-y-auto space-y-0.5 px-2">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => clsx(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-150',
                isActive
                  ? 'font-semibold'
                  : 'font-normal hover:text-slate-200'
              )}
              style={({ isActive }) => isActive
                ? { background: '#7c3aed18', color: '#a78bfa', borderLeft: '2px solid #7c3aed' }
                : { color: '#8892a4' }
              }
            >
              <Icon size={17} className="flex-shrink-0" />
              {sidebarOpen && <span>{label}</span>}
            </NavLink>
          ))}
        </nav>

        {/* User footer */}
        <div className="p-3" style={{ borderTop: '1px solid #1f2433' }}>
          <div className="flex items-center gap-3">
            <div
              className="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 text-white text-xs font-bold"
              style={{ background: 'linear-gradient(135deg, #7c3aed, #0ea5e9)' }}
            >
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </div>
            {sidebarOpen && (
              <>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-semibold text-slate-200 truncate">
                    {user?.firstName} {user?.lastName}
                  </p>
                  <p className="text-[11px] truncate" style={{ color: '#8892a4' }}>{user?.role}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="transition-colors"
                  style={{ color: '#3d4460' }}
                  onMouseEnter={e => e.currentTarget.style.color = '#f87171'}
                  onMouseLeave={e => e.currentTarget.style.color = '#3d4460'}
                >
                  <LogOut size={15} />
                </button>
              </>
            )}
          </div>
        </div>
      </aside>

      {/* ── Main ────────────────────────────────────────────────────── */}
      <div className="flex-1 flex flex-col overflow-hidden">

        {/* Topbar */}
        <header
          className="px-5 py-3 flex items-center gap-4"
          style={{ background: '#13161e', borderBottom: '1px solid #1f2433' }}
        >
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="transition-colors"
            style={{ color: '#8892a4' }}
            onMouseEnter={e => e.currentTarget.style.color = '#e2e8f0'}
            onMouseLeave={e => e.currentTarget.style.color = '#8892a4'}
          >
            {sidebarOpen ? <X size={19} /> : <Menu size={19} />}
          </button>

          <div className="flex-1" />

          {/* Balance chip */}
          <div
            className="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-lg"
            style={{ background: '#1a1d27', border: '1px solid #1f2433' }}
          >
            <span className="text-xs" style={{ color: '#8892a4' }}>Balance</span>
            <span className="text-sm font-bold tabular" style={{ color: '#34d399' }}>
              ${Number(user?.virtualBalance || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </span>
          </div>

          {/* Role badge */}
          <span
            className="text-xs px-2.5 py-1 rounded-full font-semibold"
            style={
              user?.role === 'ADMIN'
                ? { background: '#7c3aed18', color: '#a78bfa' }
                : user?.role === 'ANALYST'
                ? { background: '#f59e0b18', color: '#fbbf24' }
                : { background: '#0ea5e918', color: '#38bdf8' }
            }
          >
            {user?.role}
          </span>
        </header>

        {/* Page */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
