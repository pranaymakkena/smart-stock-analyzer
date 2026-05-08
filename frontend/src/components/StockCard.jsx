import React from 'react'
import { useNavigate } from 'react-router-dom'
import { TrendingUp, TrendingDown, Minus } from 'lucide-react'
import clsx from 'clsx'

export default function StockCard({ stock }) {
  const navigate = useNavigate()
  const change = parseFloat(stock.changePercent || 0)
  const isUp   = change > 0
  const isDown = change < 0

  return (
    <div
      onClick={() => navigate(`/stocks/${stock.symbol}`)}
      className="card cursor-pointer transition-all animate-fade-in"
      style={{ '--hover-border': '#2d3348' }}
      onMouseEnter={e => e.currentTarget.style.borderColor = '#2d3348'}
      onMouseLeave={e => e.currentTarget.style.borderColor = '#1f2433'}
    >
      <div className="flex items-start justify-between mb-3">
        <div>
          <span className="font-bold text-white text-lg">{stock.symbol}</span>
          <p className="text-xs text-slate-400 mt-0.5 truncate max-w-[140px]">{stock.companyName}</p>
        </div>
        <span className={clsx(
          'text-xs px-2 py-0.5 rounded-full',
          isUp   ? 'bg-emerald-500/10 text-emerald-400' :
          isDown ? 'bg-red-500/10 text-red-400' :
                   'bg-slate-700 text-slate-400'
        )}>
          {stock.sector}
        </span>
      </div>

      <div className="flex items-end justify-between">
        <div>
          <p className="text-2xl font-bold text-white">
            ${parseFloat(stock.currentPrice || 0).toFixed(2)}
          </p>
          <div className="flex items-center gap-1 mt-1">
            {isUp   ? <TrendingUp  size={14} className="text-emerald-400" /> :
             isDown ? <TrendingDown size={14} className="text-red-400" /> :
                      <Minus size={14} className="text-slate-400" />}
            <span className={clsx(
              'text-sm font-medium',
              isUp ? 'text-emerald-400' : isDown ? 'text-red-400' : 'text-slate-400'
            )}>
              {isUp ? '+' : ''}{change.toFixed(2)}%
            </span>
          </div>
        </div>
        <div className="text-right">
          <p className="text-xs text-slate-500">Vol</p>
          <p className="text-sm text-slate-300">
            {stock.volume ? (stock.volume / 1_000_000).toFixed(1) + 'M' : '—'}
          </p>
        </div>
      </div>

      {/* Mini price bar */}
      {stock.dayLow && stock.dayHigh && (
        <div className="mt-3">
          <div className="flex justify-between text-xs text-slate-500 mb-1">
            <span>${parseFloat(stock.dayLow).toFixed(2)}</span>
            <span>${parseFloat(stock.dayHigh).toFixed(2)}</span>
          </div>
          <div className="h-1 bg-slate-700 rounded-full overflow-hidden">
            <div
              className={clsx('h-full rounded-full', isUp ? 'bg-emerald-500' : 'bg-red-500')}
              style={{
                width: `${Math.min(100, Math.max(0,
                  ((parseFloat(stock.currentPrice) - parseFloat(stock.dayLow)) /
                   (parseFloat(stock.dayHigh) - parseFloat(stock.dayLow))) * 100
                ))}%`
              }}
            />
          </div>
        </div>
      )}
    </div>
  )
}
