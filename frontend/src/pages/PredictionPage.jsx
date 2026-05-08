import React, { useState } from 'react'
import { predictionApi, stockApi } from '../services/api'
import { Brain, TrendingUp, TrendingDown, Minus, Search } from 'lucide-react'
import clsx from 'clsx'

const STRATEGIES = [
  { value: 'moving-average', label: 'Moving Average', desc: 'MA5, MA20, MA50 crossover analysis' },
  { value: 'regression',     label: 'Linear Regression', desc: 'Statistical trend line projection' },
  { value: 'trend',          label: 'Trend Analysis', desc: 'MACD + Bollinger Bands' },
]

function PredictionCard({ result }) {
  const isUp   = result.trend === 'UPTREND'
  const isDown = result.trend === 'DOWNTREND'

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-white">{result.strategyName}</h3>
        <span className={clsx(
          'text-xs px-2 py-1 rounded-full font-semibold',
          result.recommendation === 'BUY'  ? 'bg-emerald-500/20 text-emerald-400' :
          result.recommendation === 'SELL' ? 'bg-red-500/20 text-red-400' :
                                              'bg-amber-500/20 text-amber-400'
        )}>
          {result.recommendation}
        </span>
      </div>

      <div className={clsx(
        'flex items-center justify-center gap-3 py-5 rounded-xl mb-4',
        isUp   ? 'bg-emerald-500/10' :
        isDown ? 'bg-red-500/10' : 'bg-slate-800'
      )}>
        {isUp   ? <TrendingUp  size={32} className="text-emerald-400" /> :
         isDown ? <TrendingDown size={32} className="text-red-400" /> :
                  <Minus size={32} className="text-slate-400" />}
        <div>
          <p className={clsx('text-xl font-bold',
            isUp ? 'text-emerald-400' : isDown ? 'text-red-400' : 'text-slate-300'
          )}>
            {result.trend}
          </p>
          <p className="text-xs text-slate-400">
            Confidence: {parseFloat(result.confidence || 0).toFixed(1)}%
          </p>
        </div>
      </div>

      {/* Confidence bar */}
      <div className="mb-4">
        <div className="flex justify-between text-xs text-slate-400 mb-1">
          <span>Confidence</span>
          <span>{parseFloat(result.confidence || 0).toFixed(1)}%</span>
        </div>
        <div className="h-2 bg-slate-800 rounded-full overflow-hidden">
          <div
            className={clsx('h-full rounded-full transition-all',
              isUp ? 'bg-emerald-500' : isDown ? 'bg-red-500' : 'bg-amber-500'
            )}
            style={{ width: `${Math.min(100, parseFloat(result.confidence || 0))}%` }}
          />
        </div>
      </div>

      <div className="space-y-1.5 text-sm">
        {result.predictedPrice && (
          <div className="flex justify-between">
            <span className="text-slate-400">Predicted Price</span>
            <span className="text-white font-medium">${parseFloat(result.predictedPrice).toFixed(2)}</span>
          </div>
        )}
        {result.ma5  && <div className="flex justify-between"><span className="text-slate-400">MA5</span><span className="text-white">${parseFloat(result.ma5).toFixed(2)}</span></div>}
        {result.ma20 && <div className="flex justify-between"><span className="text-slate-400">MA20</span><span className="text-white">${parseFloat(result.ma20).toFixed(2)}</span></div>}
        {result.ma50 && <div className="flex justify-between"><span className="text-slate-400">MA50</span><span className="text-white">${parseFloat(result.ma50).toFixed(2)}</span></div>}
        {result.rsi  && <div className="flex justify-between"><span className="text-slate-400">RSI</span><span className={clsx('font-medium',
          parseFloat(result.rsi) > 70 ? 'text-red-400' : parseFloat(result.rsi) < 30 ? 'text-emerald-400' : 'text-white'
        )}>{parseFloat(result.rsi).toFixed(1)}</span></div>}
        {result.macd && <div className="flex justify-between"><span className="text-slate-400">MACD</span><span className={clsx('font-medium', parseFloat(result.macd) > 0 ? 'text-emerald-400' : 'text-red-400')}>{parseFloat(result.macd).toFixed(4)}</span></div>}
        {result.bollingerUpper && <div className="flex justify-between"><span className="text-slate-400">BB Upper</span><span className="text-white">${parseFloat(result.bollingerUpper).toFixed(2)}</span></div>}
        {result.bollingerLower && <div className="flex justify-between"><span className="text-slate-400">BB Lower</span><span className="text-white">${parseFloat(result.bollingerLower).toFixed(2)}</span></div>}
      </div>

      {result.analysis && (
        <p className="text-xs text-slate-500 mt-4 leading-relaxed border-t border-slate-800 pt-3">
          {result.analysis}
        </p>
      )}
    </div>
  )
}

export default function PredictionPage() {
  const [symbol, setSymbol]       = useState('')
  const [results, setResults]     = useState([])
  const [loading, setLoading]     = useState(false)
  const [searched, setSearched]   = useState(false)

  const handlePredict = async () => {
    if (!symbol.trim()) return
    setLoading(true)
    setSearched(true)
    try {
      const res = await predictionApi.predictAll(symbol.toUpperCase())
      setResults(res.data.data || [])
    } catch {
      setResults([])
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-white">Price Predictions</h1>
        <p className="text-slate-400 text-sm mt-1">
          AI-like trend analysis using Moving Average, Linear Regression & MACD
        </p>
      </div>

      {/* Strategy info */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {STRATEGIES.map(s => (
          <div key={s.value} className="card border-slate-700">
            <div className="flex items-center gap-2 mb-2">
              <Brain size={16} className="text-purple-400" />
              <span className="font-medium text-white text-sm">{s.label}</span>
            </div>
            <p className="text-xs text-slate-400">{s.desc}</p>
          </div>
        ))}
      </div>

      {/* Search */}
      <div className="flex gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            className="input pl-9"
            placeholder="Enter stock symbol (e.g. AAPL)"
            value={symbol}
            onChange={e => setSymbol(e.target.value.toUpperCase())}
            onKeyDown={e => e.key === 'Enter' && handlePredict()}
          />
        </div>
        <button onClick={handlePredict} disabled={loading || !symbol} className="btn-primary px-6">
          {loading ? 'Analyzing...' : 'Analyze'}
        </button>
      </div>

      {/* Quick symbols */}
      <div className="flex gap-2 flex-wrap">
        {['AAPL', 'TSLA', 'NVDA', 'MSFT', 'GOOGL', 'AMZN'].map(s => (
          <button
            key={s}
            onClick={() => { setSymbol(s); }}
            className="text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1.5 rounded-lg transition-colors"
          >
            {s}
          </button>
        ))}
      </div>

      {loading && (
        <div className="flex items-center justify-center h-48">
          <div className="text-center">
            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-purple-500 mx-auto mb-3" />
            <p className="text-slate-400 text-sm">Running prediction algorithms...</p>
          </div>
        </div>
      )}

      {!loading && searched && results.length === 0 && (
        <div className="card text-center py-12 text-slate-500">
          No prediction data available for {symbol}
        </div>
      )}

      {!loading && results.length > 0 && (
        <div>
          <h2 className="font-semibold text-white mb-4">
            Predictions for <span className="text-violet-400">{symbol}</span>
          </h2>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {results.map((r, i) => <PredictionCard key={i} result={r} />)}
          </div>
        </div>
      )}
    </div>
  )
}
