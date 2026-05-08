import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { stockApi, portfolioApi, predictionApi, watchlistApi } from '../services/api'
import StockChart from '../components/StockChart'
import { TrendingUp, TrendingDown, Star, ShoppingCart, ArrowLeft, Brain, AlertTriangle } from 'lucide-react'
import toast from 'react-hot-toast'
import clsx from 'clsx'

function InfoRow({ label, value, color }) {
  return (
    <div className="flex justify-between py-2 border-b border-slate-800 last:border-0">
      <span className="text-sm text-slate-400">{label}</span>
      <span className={clsx('text-sm font-medium', color || 'text-white')}>{value}</span>
    </div>
  )
}

export default function StockDetailPage() {
  const { symbol } = useParams()
  const navigate   = useNavigate()
  const [stock, setStock]           = useState(null)
  const [prediction, setPrediction] = useState(null)
  const [risk, setRisk]             = useState(null)
  const [loading, setLoading]       = useState(true)
  const [tradeQty, setTradeQty]     = useState(1)
  const [tradeLoading, setTradeLoading] = useState(false)
  const [watchlists, setWatchlists] = useState([])

  useEffect(() => {
    Promise.all([
      stockApi.getBySymbol(symbol),
      predictionApi.predictAll(symbol),
      predictionApi.risk(symbol),
      watchlistApi.getAll(),
    ]).then(([s, p, r, w]) => {
      setStock(s.data.data)
      setPrediction(p.data.data?.[0])
      setRisk(r.data.data)
      setWatchlists(w.data.data || [])
    }).catch(() => {}).finally(() => setLoading(false))
  }, [symbol])

  const handleBuy = async () => {
    setTradeLoading(true)
    try {
      await portfolioApi.buy({ symbol, quantity: tradeQty })
      toast.success(`Bought ${tradeQty} shares of ${symbol}`)
    } catch {} finally { setTradeLoading(false) }
  }

  const handleSell = async () => {
    setTradeLoading(true)
    try {
      await portfolioApi.sell({ symbol, quantity: tradeQty })
      toast.success(`Sold ${tradeQty} shares of ${symbol}`)
    } catch {} finally { setTradeLoading(false) }
  }

  const handleAddToWatchlist = async () => {
    if (watchlists.length === 0) {
      toast.error('Create a watchlist first')
      return
    }
    try {
      await watchlistApi.addStock(watchlists[0].id, symbol)
      toast.success(`${symbol} added to watchlist`)
    } catch {}
  }

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
    </div>
  )

  if (!stock) return (
    <div className="text-center py-16 text-slate-500">Stock not found</div>
  )

  const change = parseFloat(stock.changePercent || 0)
  const isUp   = change >= 0

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Back */}
      <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors text-sm">
        <ArrowLeft size={16} /> Back
      </button>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-white">{stock.symbol}</h1>
            <span className="text-xs bg-slate-800 text-slate-400 px-2 py-1 rounded">{stock.sector}</span>
          </div>
          <p className="text-slate-400 mt-1">{stock.companyName}</p>
          <div className="flex items-center gap-3 mt-2">
            <span className="text-3xl font-bold text-white">
              ${parseFloat(stock.currentPrice || 0).toFixed(2)}
            </span>
            <span className={clsx('flex items-center gap-1 text-lg font-semibold',
              isUp ? 'text-emerald-400' : 'text-red-400')}>
              {isUp ? <TrendingUp size={20} /> : <TrendingDown size={20} />}
              {isUp ? '+' : ''}{change.toFixed(2)}%
            </span>
          </div>
        </div>

        {/* Trade panel */}
        <div className="card min-w-[220px]">
          <p className="text-sm text-slate-400 mb-3">Virtual Trade</p>
          <div className="flex items-center gap-2 mb-3">
            <button onClick={() => setTradeQty(Math.max(1, tradeQty - 1))}
              className="w-8 h-8 bg-slate-800 rounded-lg text-white hover:bg-slate-700 transition-colors">−</button>
            <input
              type="number" min="1" value={tradeQty}
              onChange={e => setTradeQty(Math.max(1, parseInt(e.target.value) || 1))}
              className="input text-center w-16 py-1"
            />
            <button onClick={() => setTradeQty(tradeQty + 1)}
              className="w-8 h-8 bg-slate-800 rounded-lg text-white hover:bg-slate-700 transition-colors">+</button>
          </div>
          <p className="text-xs text-slate-400 mb-3">
            Total: <span className="text-white font-semibold">
              ${(parseFloat(stock.currentPrice || 0) * tradeQty).toFixed(2)}
            </span>
          </p>
          <div className="flex gap-2">
            <button onClick={handleBuy} disabled={tradeLoading}
              className="btn-success flex-1 text-sm py-2">Buy</button>
            <button onClick={handleSell} disabled={tradeLoading}
              className="btn-danger flex-1 text-sm py-2">Sell</button>
          </div>
          <button onClick={handleAddToWatchlist}
            className="btn-secondary w-full mt-2 text-sm py-2 flex items-center justify-center gap-2">
            <Star size={14} /> Watchlist
          </button>
        </div>
      </div>

      {/* Chart */}
      <StockChart symbol={symbol} currentPrice={stock.currentPrice} />

      {/* Stats grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Key stats */}
        <div className="card">
          <h3 className="font-semibold text-white mb-3">Key Statistics</h3>
          <InfoRow label="Open"          value={`$${parseFloat(stock.openPrice || 0).toFixed(2)}`} />
          <InfoRow label="Day High"      value={`$${parseFloat(stock.dayHigh || 0).toFixed(2)}`} color="text-emerald-400" />
          <InfoRow label="Day Low"       value={`$${parseFloat(stock.dayLow || 0).toFixed(2)}`}  color="text-red-400" />
          <InfoRow label="Prev Close"    value={`$${parseFloat(stock.previousClose || 0).toFixed(2)}`} />
          <InfoRow label="52W High"      value={`$${parseFloat(stock.fiftyTwoWeekHigh || 0).toFixed(2)}`} />
          <InfoRow label="52W Low"       value={`$${parseFloat(stock.fiftyTwoWeekLow || 0).toFixed(2)}`} />
          <InfoRow label="Volume"        value={stock.volume ? (stock.volume / 1_000_000).toFixed(2) + 'M' : '—'} />
          <InfoRow label="Market Cap"    value={stock.marketCap ? '$' + (parseFloat(stock.marketCap) / 1e12).toFixed(2) + 'T' : '—'} />
          <InfoRow label="P/E Ratio"     value={stock.peRatio ? parseFloat(stock.peRatio).toFixed(2) : '—'} />
        </div>

        {/* Prediction */}
        {prediction && (
          <div className="card">
            <div className="flex items-center gap-2 mb-3">
              <Brain size={18} className="text-purple-400" />
              <h3 className="font-semibold text-white">AI Prediction</h3>
            </div>
            <div className={clsx(
              'text-center py-4 rounded-xl mb-4',
              prediction.trend === 'UPTREND'   ? 'bg-emerald-500/10 border border-emerald-500/20' :
              prediction.trend === 'DOWNTREND' ? 'bg-red-500/10 border border-red-500/20' :
                                                  'bg-slate-800 border border-slate-700'
            )}>
              <p className={clsx('text-2xl font-bold',
                prediction.trend === 'UPTREND'   ? 'text-emerald-400' :
                prediction.trend === 'DOWNTREND' ? 'text-red-400' : 'text-slate-300'
              )}>
                {prediction.trend}
              </p>
              <p className="text-sm text-slate-400 mt-1">{prediction.strategyName}</p>
            </div>
            <InfoRow label="Predicted Price" value={`$${parseFloat(prediction.predictedPrice || 0).toFixed(2)}`} />
            <InfoRow label="Confidence"      value={`${parseFloat(prediction.confidence || 0).toFixed(1)}%`} />
            <InfoRow label="Recommendation"  value={prediction.recommendation}
              color={prediction.recommendation === 'BUY' ? 'text-emerald-400' :
                     prediction.recommendation === 'SELL' ? 'text-red-400' : 'text-amber-400'} />
            {prediction.ma5  && <InfoRow label="MA5"  value={`$${parseFloat(prediction.ma5).toFixed(2)}`} />}
            {prediction.ma20 && <InfoRow label="MA20" value={`$${parseFloat(prediction.ma20).toFixed(2)}`} />}
            {prediction.rsi  && <InfoRow label="RSI"  value={parseFloat(prediction.rsi).toFixed(1)} />}
            <p className="text-xs text-slate-500 mt-3 leading-relaxed">{prediction.analysis}</p>
          </div>
        )}

        {/* Risk */}
        {risk && (
          <div className="card">
            <div className="flex items-center gap-2 mb-3">
              <AlertTriangle size={18} className="text-amber-400" />
              <h3 className="font-semibold text-white">Risk Analysis</h3>
            </div>
            <div className="text-center py-4 mb-4">
              <div className={clsx(
                'inline-flex items-center justify-center w-16 h-16 rounded-full text-2xl font-bold mb-2',
                risk.riskScore <= 3 ? 'bg-emerald-500/20 text-emerald-400' :
                risk.riskScore <= 6 ? 'bg-amber-500/20 text-amber-400' :
                                      'bg-red-500/20 text-red-400'
              )}>
                {risk.riskScore}
              </div>
              <p className="text-sm font-semibold text-white">{risk.riskLevel}</p>
              <p className="text-xs text-slate-400">Risk Score / 10</p>
            </div>
            {risk.volatility  && <InfoRow label="Volatility"   value={`${(parseFloat(risk.volatility) * 100).toFixed(1)}%`} />}
            {risk.sharpeRatio && <InfoRow label="Sharpe Ratio" value={parseFloat(risk.sharpeRatio).toFixed(2)} />}
            {risk.maxDrawdown && <InfoRow label="Max Drawdown" value={`${(parseFloat(risk.maxDrawdown) * 100).toFixed(1)}%`} color="text-red-400" />}
            <p className="text-xs text-slate-500 mt-3 leading-relaxed">{risk.riskSummary}</p>
          </div>
        )}
      </div>
    </div>
  )
}
