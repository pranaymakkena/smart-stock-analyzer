import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { stockApi, portfolioApi, predictionApi } from '../services/api'
import StockCard from '../components/StockCard'
import {
  TrendingUp, TrendingDown, DollarSign, BarChart2,
  ArrowUpRight, ArrowDownRight, Lightbulb, RefreshCw
} from 'lucide-react'
import { AreaChart, Area, ResponsiveContainer, Tooltip } from 'recharts'
import toast from 'react-hot-toast'

function StatCard({ title, value, sub, icon: Icon, color, trend }) {
  return (
    <div className="card">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs text-slate-400 mb-1">{title}</p>
          <p className="text-2xl font-bold text-white">{value}</p>
          {sub && <p className={`text-xs mt-1 ${trend === 'up' ? 'text-emerald-400' : trend === 'down' ? 'text-red-400' : 'text-slate-400'}`}>{sub}</p>}
        </div>
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${color}`}>
          <Icon size={20} className="text-white" />
        </div>
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { user, refreshUser } = useAuth()
  const navigate = useNavigate()
  const [summary, setSummary]           = useState(null)
  const [portfolios, setPortfolios]     = useState([])
  const [recommendations, setRecs]      = useState(null)
  const [loading, setLoading]           = useState(true)

  useEffect(() => {
    Promise.all([
      stockApi.getMarketSummary(),
      portfolioApi.getAll(),
      predictionApi.recommendations(),
    ]).then(([s, p, r]) => {
      setSummary(s.data.data)
      setPortfolios(p.data.data || [])
      setRecs(r.data.data)
    }).catch(() => {}).finally(() => setLoading(false))
  }, [])

  const totalPortfolioValue = portfolios.reduce((sum, p) => sum + parseFloat(p.totalValue || 0), 0)
  const totalProfit = portfolios.reduce((sum, p) => sum + parseFloat(p.totalProfit || 0), 0)

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
    </div>
  )

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">
            Good morning, {user?.firstName} 👋
          </h1>
          <p className="text-slate-400 text-sm mt-1">Here's your market overview</p>
        </div>
        <button
          onClick={() => { refreshUser(); toast.success('Refreshed!') }}
          className="btn-secondary flex items-center gap-2 text-sm"
        >
          <RefreshCw size={14} /> Refresh
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Virtual Balance"
          value={`$${Number(user?.virtualBalance || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
          sub="Available to invest"
          icon={DollarSign}
          color="bg-violet-600"
        />
        <StatCard
          title="Portfolio Value"
          value={`$${totalPortfolioValue.toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
          sub={`${portfolios.length} portfolio${portfolios.length !== 1 ? 's' : ''}`}
          icon={BarChart2}
          color="bg-purple-600"
        />
        <StatCard
          title="Total P&L"
          value={`${totalProfit >= 0 ? '+' : ''}$${Math.abs(totalProfit).toLocaleString('en-US', { minimumFractionDigits: 2 })}`}
          sub={totalProfit >= 0 ? '▲ Profit' : '▼ Loss'}
          icon={totalProfit >= 0 ? TrendingUp : TrendingDown}
          color={totalProfit >= 0 ? 'bg-emerald-600' : 'bg-red-600'}
          trend={totalProfit >= 0 ? 'up' : 'down'}
        />
        <StatCard
          title="Market"
          value={`${summary?.gainersCount || 0}↑ / ${summary?.losersCount || 0}↓`}
          sub={`${summary?.totalStocksTracked || 0} stocks tracked`}
          icon={BarChart2}
          color="bg-amber-600"
        />
      </div>

      {/* Top Gainers & Losers */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <div className="flex items-center gap-2 mb-3">
            <ArrowUpRight size={18} className="text-emerald-400" />
            <h2 className="font-semibold text-white">Top Gainers</h2>
          </div>
          <div className="space-y-2">
            {(summary?.topGainers || []).slice(0, 5).map(stock => (
              <div
                key={stock.symbol}
                onClick={() => navigate(`/stocks/${stock.symbol}`)}
                className="flex items-center justify-between bg-slate-900 border border-slate-800 rounded-lg px-4 py-3 cursor-pointer hover:border-emerald-800 transition-colors"
              >
                <div>
                  <span className="font-semibold text-white">{stock.symbol}</span>
                  <p className="text-xs text-slate-400">{stock.companyName}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-white">${parseFloat(stock.currentPrice || 0).toFixed(2)}</p>
                  <span className="badge-up">+{parseFloat(stock.changePercent || 0).toFixed(2)}%</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div>
          <div className="flex items-center gap-2 mb-3">
            <ArrowDownRight size={18} className="text-red-400" />
            <h2 className="font-semibold text-white">Top Losers</h2>
          </div>
          <div className="space-y-2">
            {(summary?.topLosers || []).slice(0, 5).map(stock => (
              <div
                key={stock.symbol}
                onClick={() => navigate(`/stocks/${stock.symbol}`)}
                className="flex items-center justify-between bg-slate-900 border border-slate-800 rounded-lg px-4 py-3 cursor-pointer hover:border-red-800 transition-colors"
              >
                <div>
                  <span className="font-semibold text-white">{stock.symbol}</span>
                  <p className="text-xs text-slate-400">{stock.companyName}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-white">${parseFloat(stock.currentPrice || 0).toFixed(2)}</p>
                  <span className="badge-down">{parseFloat(stock.changePercent || 0).toFixed(2)}%</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* AI Recommendations */}
      {recommendations?.insights?.length > 0 && (
        <div className="card">
          <div className="flex items-center gap-2 mb-4">
            <Lightbulb size={18} className="text-amber-400" />
            <h2 className="font-semibold text-white">Smart Insights</h2>
          </div>
          <div className="space-y-2">
            {recommendations.insights.map((insight, i) => (
              <div key={i} className="flex items-start gap-3 bg-slate-800/50 rounded-lg p-3">
                <span className="text-amber-400 mt-0.5">💡</span>
                <p className="text-sm text-slate-300">{insight}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Most Active */}
      <div>
        <h2 className="font-semibold text-white mb-3">Most Active Stocks</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {(summary?.mostActive || []).slice(0, 8).map(stock => (
            <StockCard key={stock.symbol} stock={stock} />
          ))}
        </div>
      </div>
    </div>
  )
}
