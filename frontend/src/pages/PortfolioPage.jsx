import React, { useEffect, useState } from 'react'
import { portfolioApi } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Briefcase, TrendingUp, TrendingDown, Plus } from 'lucide-react'
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts'
import toast from 'react-hot-toast'

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']

export default function PortfolioPage() {
  const { user, refreshUser } = useAuth()
  const [portfolios, setPortfolios] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadPortfolios()
  }, [])

  const loadPortfolios = () => {
    portfolioApi.getAll()
      .then(res => setPortfolios(res.data.data || []))
      .finally(() => setLoading(false))
  }

  const handleCreate = async () => {
    const name = prompt('Portfolio name:')
    if (!name) return
    try {
      await portfolioApi.create(name)
      toast.success('Portfolio created')
      loadPortfolios()
    } catch {}
  }

  const handleRefresh = async () => {
    try {
      await portfolioApi.refresh()
      await refreshUser()
      loadPortfolios()
      toast.success('Portfolio refreshed')
    } catch {}
  }

  if (loading) return <div className="flex items-center justify-center h-64">
    <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
  </div>

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white">My Portfolio</h1>
        <div className="flex gap-2">
          <button onClick={handleRefresh} className="btn-secondary text-sm">Refresh</button>
          <button onClick={handleCreate} className="btn-primary text-sm flex items-center gap-2">
            <Plus size={14} /> New Portfolio
          </button>
        </div>
      </div>

      {portfolios.length === 0 ? (
        <div className="card text-center py-16">
          <Briefcase size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400 mb-4">No portfolios yet</p>
          <button onClick={handleCreate} className="btn-primary">Create Your First Portfolio</button>
        </div>
      ) : (
        <div className="space-y-6">
          {portfolios.map(p => {
            const profit = parseFloat(p.totalProfit || 0)
            const isUp = profit >= 0
            const chartData = (p.items || []).map((item, i) => ({
              name: item.symbol,
              value: parseFloat(item.currentValue || 0),
              color: COLORS[i % COLORS.length]
            }))

            return (
              <div key={p.id} className="card">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h2 className="text-xl font-bold text-white">{p.name}</h2>
                    <p className="text-sm text-slate-400 mt-1">{p.items?.length || 0} holdings</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-white">
                      ${parseFloat(p.totalValue || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </p>
                    <div className={`flex items-center gap-1 justify-end mt-1 ${isUp ? 'text-emerald-400' : 'text-red-400'}`}>
                      {isUp ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                      <span className="text-sm font-semibold">
                        {isUp ? '+' : ''}${Math.abs(profit).toFixed(2)} ({parseFloat(p.profitPercent || 0).toFixed(2)}%)
                      </span>
                    </div>
                  </div>
                </div>

                {chartData.length > 0 && (
                  <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div className="h-48">
                      <ResponsiveContainer width="100%" height="100%">
                        <PieChart>
                          <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={60}>
                            {chartData.map((entry, i) => <Cell key={i} fill={entry.color} />)}
                          </Pie>
                          <Tooltip formatter={v => `$${parseFloat(v).toFixed(2)}`} />
                        </PieChart>
                      </ResponsiveContainer>
                    </div>

                    <div className="lg:col-span-2 overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead className="text-left text-slate-400 border-b border-slate-800">
                          <tr>
                            <th className="pb-2">Symbol</th>
                            <th className="pb-2">Qty</th>
                            <th className="pb-2">Avg Price</th>
                            <th className="pb-2">Current</th>
                            <th className="pb-2">Value</th>
                            <th className="pb-2">P&L</th>
                          </tr>
                        </thead>
                        <tbody>
                          {(p.items || []).map(item => {
                            const pl = parseFloat(item.profitLoss || 0)
                            return (
                              <tr key={item.id} className="border-b border-slate-800 last:border-0">
                                <td className="py-2 font-semibold text-white">{item.symbol}</td>
                                <td className="py-2 text-slate-300">{item.quantity}</td>
                                <td className="py-2 text-slate-300">${parseFloat(item.avgBuyPrice || 0).toFixed(2)}</td>
                                <td className="py-2 text-slate-300">${parseFloat(item.currentPrice || 0).toFixed(2)}</td>
                                <td className="py-2 text-white font-medium">${parseFloat(item.currentValue || 0).toFixed(2)}</td>
                                <td className={`py-2 font-semibold ${pl >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                                  {pl >= 0 ? '+' : ''}${pl.toFixed(2)}
                                </td>
                              </tr>
                            )
                          })}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
