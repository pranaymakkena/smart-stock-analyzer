import React, { useEffect, useState } from 'react'
import { watchlistApi } from '../services/api'
import StockCard from '../components/StockCard'
import { Star, Plus, Trash2 } from 'lucide-react'
import toast from 'react-hot-toast'

export default function WatchlistPage() {
  const [watchlists, setWatchlists] = useState([])
  const [selected, setSelected]     = useState(null)
  const [stocks, setStocks]         = useState([])
  const [loading, setLoading]       = useState(true)

  useEffect(() => {
    loadWatchlists()
  }, [])

  const loadWatchlists = async () => {
    try {
      const res = await watchlistApi.getAll()
      const wl = res.data.data || []
      setWatchlists(wl)
      if (wl.length > 0) {
        setSelected(wl[0])
        loadStocks(wl[0].id)
      }
    } finally {
      setLoading(false)
    }
  }

  const loadStocks = async (id) => {
    try {
      const res = await watchlistApi.getStocks(id)
      setStocks(res.data.data || [])
    } catch {
      setStocks([])
    }
  }

  const handleCreate = async () => {
    const name = prompt('Watchlist name:')
    if (!name) return
    try {
      await watchlistApi.create(name)
      toast.success('Watchlist created')
      loadWatchlists()
    } catch {}
  }

  const handleAddStock = async () => {
    if (!selected) return
    const sym = prompt('Stock symbol:')
    if (!sym) return
    try {
      await watchlistApi.addStock(selected.id, sym.toUpperCase())
      toast.success(`${sym.toUpperCase()} added`)
      loadStocks(selected.id)
    } catch {}
  }

  const handleRemoveStock = async (symbol) => {
    if (!selected) return
    try {
      await watchlistApi.removeStock(selected.id, symbol)
      toast.success(`${symbol} removed`)
      loadStocks(selected.id)
    } catch {}
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this watchlist?')) return
    try {
      await watchlistApi.delete(id)
      toast.success('Watchlist deleted')
      setSelected(null)
      setStocks([])
      loadWatchlists()
    } catch {}
  }

  const selectWatchlist = (wl) => {
    setSelected(wl)
    loadStocks(wl.id)
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white">Watchlists</h1>
        <button onClick={handleCreate} className="btn-primary text-sm flex items-center gap-2">
          <Plus size={14} /> New Watchlist
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
        </div>
      ) : watchlists.length === 0 ? (
        <div className="card text-center py-16">
          <Star size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400 mb-4">No watchlists yet</p>
          <button onClick={handleCreate} className="btn-primary">Create Watchlist</button>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Sidebar */}
          <div className="space-y-2">
            {watchlists.map(wl => (
              <div
                key={wl.id}
                onClick={() => selectWatchlist(wl)}
                className={`flex items-center justify-between p-3 rounded-lg cursor-pointer transition-colors ${
                  selected?.id === wl.id
                    ? 'bg-violet-600/20 border border-violet-600/30 text-violet-400'
                    : 'bg-slate-900 border border-slate-800 text-slate-300 hover:border-slate-700'
                }`}
              >
                <div className="flex items-center gap-2">
                  <Star size={14} />
                  <span className="text-sm font-medium">{wl.name}</span>
                </div>
                <button
                  onClick={e => { e.stopPropagation(); handleDelete(wl.id) }}
                  className="text-slate-500 hover:text-red-400 transition-colors"
                >
                  <Trash2 size={14} />
                </button>
              </div>
            ))}
          </div>

          {/* Stocks */}
          <div className="lg:col-span-3">
            {selected && (
              <>
                <div className="flex items-center justify-between mb-4">
                  <h2 className="font-semibold text-white">{selected.name}</h2>
                  <button onClick={handleAddStock} className="btn-secondary text-sm flex items-center gap-2">
                    <Plus size={14} /> Add Stock
                  </button>
                </div>
                {stocks.length === 0 ? (
                  <div className="card text-center py-12 text-slate-500">
                    No stocks in this watchlist. Add some!
                  </div>
                ) : (
                  <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
                    {stocks.map(stock => (
                      <div key={stock.symbol} className="relative">
                        <StockCard stock={stock} />
                        <button
                          onClick={() => handleRemoveStock(stock.symbol)}
                          className="absolute top-2 right-2 text-slate-500 hover:text-red-400 transition-colors bg-slate-900 rounded p-1"
                        >
                          <Trash2 size={12} />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
