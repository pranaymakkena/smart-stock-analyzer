import React, { useEffect, useState } from 'react'
import { adminApi } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Trophy, Medal, TrendingUp, TrendingDown } from 'lucide-react'
import clsx from 'clsx'

export default function LeaderboardPage() {
  const { user } = useAuth()
  const [leaderboard, setLeaderboard] = useState([])
  const [loading, setLoading]         = useState(true)
  const [error, setError]             = useState(null)

  useEffect(() => {
    adminApi.getLeaderboard()
      .then(res => setLeaderboard(res.data.data || []))
      .catch(err => {
        if (err.response?.status === 403) {
          setError('Leaderboard is only visible to Admins.')
        }
      })
      .finally(() => setLoading(false))
  }, [])

  const rankIcon = (rank) => {
    if (rank === 1) return <Trophy size={20} className="text-yellow-400" />
    if (rank === 2) return <Medal size={20} className="text-slate-300" />
    if (rank === 3) return <Medal size={20} className="text-amber-600" />
    return <span className="text-slate-400 font-bold w-5 text-center">{rank}</span>
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-white">Leaderboard</h1>
        <p className="text-slate-400 text-sm mt-1">Virtual trading competition rankings</p>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-48">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-yellow-500" />
        </div>
      ) : error ? (
        <div className="card text-center py-16">
          <Trophy size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400">{error}</p>
          <p className="text-slate-500 text-sm mt-2">Login as Admin to view the leaderboard.</p>
        </div>
      ) : leaderboard.length === 0 ? (
        <div className="card text-center py-16">
          <Trophy size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400">No rankings yet. Start trading!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {leaderboard.map(entry => {
            const isCurrentUser = entry.userId === user?.id
            const profit = parseFloat(entry.totalProfit || 0)
            const isUp = profit >= 0

            return (
              <div key={entry.userId} className={clsx(
                'card flex items-center gap-4 transition-all',
                isCurrentUser ? 'border-violet-600/50 bg-blue-900/10' : '',
                entry.rank <= 3 ? 'border-yellow-600/30' : ''
              )}>
                <div className="flex items-center justify-center w-8">
                  {rankIcon(entry.rank)}
                </div>

                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-white">{entry.fullName}</span>
                    {isCurrentUser && (
                      <span className="text-xs bg-violet-500/20 text-violet-400 px-2 py-0.5 rounded">You</span>
                    )}
                  </div>
                  <p className="text-xs text-slate-400 mt-0.5">{entry.totalTrades} trades</p>
                </div>

                <div className="text-right">
                  <div className={clsx('flex items-center gap-1 justify-end font-semibold',
                    isUp ? 'text-emerald-400' : 'text-red-400')}>
                    {isUp ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                    {isUp ? '+' : ''}${Math.abs(profit).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                  </div>
                  <p className="text-xs text-slate-400 mt-0.5">
                    {parseFloat(entry.profitPercent || 0).toFixed(2)}% return
                  </p>
                </div>

                <div className="text-right hidden sm:block">
                  <p className="text-sm font-semibold text-white">
                    ${parseFloat(entry.portfolioValue || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                  </p>
                  <p className="text-xs text-slate-400">Portfolio Value</p>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
