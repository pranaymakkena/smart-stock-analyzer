import React, { useEffect, useState } from 'react'
import { stockApi } from '../services/api'
import StockCard from '../components/StockCard'
import { Search, Filter } from 'lucide-react'

const SECTORS = ['ALL', 'TECHNOLOGY', 'BANKING', 'PHARMA', 'ENERGY', 'AUTOMOTIVE', 'RETAIL', 'CONSUMER_GOODS']

export default function MarketPage() {
  const [stocks, setStocks]     = useState([])
  const [filtered, setFiltered] = useState([])
  const [query, setQuery]       = useState('')
  const [sector, setSector]     = useState('ALL')
  const [loading, setLoading]   = useState(true)
  const [sortBy, setSortBy]     = useState('symbol')

  useEffect(() => {
    stockApi.getAll()
      .then(res => {
        setStocks(res.data.data || [])
        setFiltered(res.data.data || [])
      })
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    let result = [...stocks]
    if (sector !== 'ALL') result = result.filter(s => s.sector === sector)
    if (query) result = result.filter(s =>
      s.symbol.toLowerCase().includes(query.toLowerCase()) ||
      s.companyName.toLowerCase().includes(query.toLowerCase())
    )
    result.sort((a, b) => {
      if (sortBy === 'price')   return parseFloat(b.currentPrice || 0) - parseFloat(a.currentPrice || 0)
      if (sortBy === 'change')  return parseFloat(b.changePercent || 0) - parseFloat(a.changePercent || 0)
      if (sortBy === 'volume')  return (b.volume || 0) - (a.volume || 0)
      return a.symbol.localeCompare(b.symbol)
    })
    setFiltered(result)
  }, [stocks, query, sector, sortBy])

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white">Market Overview</h1>
        <span className="text-sm text-slate-400">{filtered.length} stocks</span>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            className="input pl-9"
            placeholder="Search by symbol or company..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
        </div>
        <select className="input sm:w-48" value={sortBy} onChange={e => setSortBy(e.target.value)}>
          <option value="symbol">Sort: Symbol</option>
          <option value="price">Sort: Price</option>
          <option value="change">Sort: Change %</option>
          <option value="volume">Sort: Volume</option>
        </select>
      </div>

      {/* Sector tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1">
        {SECTORS.map(s => (
          <button
            key={s}
            onClick={() => setSector(s)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-colors ${
              sector === s
                ? 'bg-violet-600 text-white'
                : 'bg-slate-800 text-slate-400 hover:text-white'
            }`}
          >
            {s === 'ALL' ? 'All Sectors' : s.replace('_', ' ')}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {filtered.map(stock => <StockCard key={stock.symbol} stock={stock} />)}
          {filtered.length === 0 && (
            <div className="col-span-full text-center py-16 text-slate-500">
              No stocks found matching your criteria
            </div>
          )}
        </div>
      )}
    </div>
  )
}
