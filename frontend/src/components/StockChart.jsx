import React, { useState, useEffect } from 'react'
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, ReferenceLine, BarChart, Bar, ComposedChart, Line
} from 'recharts'
import { stockApi } from '../services/api'
import { format } from 'date-fns'

const PERIODS = [
  { label: '1W', value: '1w' },
  { label: '1M', value: '1m' },
  { label: '3M', value: '3m' },
  { label: '6M', value: '6m' },
  { label: '1Y', value: '1y' },
]

const CustomTooltip = ({ active, payload, label }) => {
  if (!active || !payload?.length) return null
  const d = payload[0]?.payload
  return (
    <div className="bg-slate-800 border border-slate-700 rounded-lg p-3 text-xs">
      <p className="text-slate-400 mb-2">{label}</p>
      <div className="space-y-1">
        <p className="text-white">Close: <span className="font-bold">${parseFloat(d?.close || 0).toFixed(2)}</span></p>
        <p className="text-emerald-400">High: ${parseFloat(d?.high || 0).toFixed(2)}</p>
        <p className="text-red-400">Low: ${parseFloat(d?.low || 0).toFixed(2)}</p>
        <p className="text-slate-400">Vol: {d?.volume ? (d.volume / 1_000_000).toFixed(2) + 'M' : '—'}</p>
      </div>
    </div>
  )
}

export default function StockChart({ symbol, currentPrice }) {
  const [period, setPeriod]   = useState('1m')
  const [data, setData]       = useState([])
  const [loading, setLoading] = useState(true)
  const [chartType, setChartType] = useState('area')

  useEffect(() => {
    setLoading(true)
    stockApi.getHistory(symbol, period)
      .then(res => {
        const raw = res.data.data || []
        setData(raw.map(h => ({
          date:   format(new Date(h.date), period === '1w' ? 'EEE' : period === '1m' ? 'MMM d' : 'MMM yy'),
          close:  parseFloat(h.close),
          open:   parseFloat(h.open),
          high:   parseFloat(h.high),
          low:    parseFloat(h.low),
          volume: h.volume,
        })))
      })
      .catch(() => setData([]))
      .finally(() => setLoading(false))
  }, [symbol, period])

  const isPositive = data.length > 1 && data[data.length - 1]?.close >= data[0]?.close
  const color = isPositive ? '#10b981' : '#ef4444'

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-white">Price Chart</h3>
        <div className="flex items-center gap-2">
          {/* Chart type toggle */}
          <div className="flex bg-slate-800 rounded-lg p-0.5 text-xs">
            {['area', 'candle'].map(t => (
              <button
                key={t}
                onClick={() => setChartType(t)}
                className={`px-2 py-1 rounded capitalize transition-colors ${
                  chartType === t ? 'bg-slate-600 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {t}
              </button>
            ))}
          </div>
          {/* Period selector */}
          <div className="flex bg-slate-800 rounded-lg p-0.5 text-xs">
            {PERIODS.map(p => (
              <button
                key={p.value}
                onClick={() => setPeriod(p.value)}
                className={`px-2.5 py-1 rounded transition-colors ${
                  period === p.value ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-white'
                }`}
              >
                {p.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      {loading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500" />
        </div>
      ) : data.length === 0 ? (
        <div className="h-64 flex items-center justify-center text-slate-500">
          No historical data available
        </div>
      ) : (
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <ComposedChart data={data} margin={{ top: 5, right: 5, bottom: 5, left: 0 }}>
              <defs>
                <linearGradient id="colorClose" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor={color} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={color} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
              <XAxis dataKey="date" tick={{ fill: '#64748b', fontSize: 11 }} tickLine={false} />
              <YAxis
                domain={['auto', 'auto']}
                tick={{ fill: '#64748b', fontSize: 11 }}
                tickLine={false}
                tickFormatter={v => `$${v.toFixed(0)}`}
                width={60}
              />
              <Tooltip content={<CustomTooltip />} />
              <Area
                type="monotone"
                dataKey="close"
                stroke={color}
                strokeWidth={2}
                fill="url(#colorClose)"
                dot={false}
              />
            </ComposedChart>
          </ResponsiveContainer>
        </div>
      )}

      {/* Volume chart */}
      {data.length > 0 && (
        <div className="h-16 mt-2">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} margin={{ top: 0, right: 5, bottom: 0, left: 0 }}>
              <Bar dataKey="volume" fill="#334155" radius={[2, 2, 0, 0]} />
              <XAxis dataKey="date" hide />
              <YAxis hide />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  )
}
