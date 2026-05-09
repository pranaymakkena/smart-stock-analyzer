import React, { useEffect, useState } from 'react'
import { portfolioApi } from '../services/api'
import { ArrowUpRight, ArrowDownRight, ArrowLeftRight } from 'lucide-react'
import clsx from 'clsx'

export default function TransactionsPage() {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading]           = useState(true)
  const [filter, setFilter]             = useState('ALL')

  useEffect(() => {
    portfolioApi.transactions()
      .then(res => setTransactions(res.data.data || []))
      .finally(() => setLoading(false))
  }, [])

  const filtered = filter === 'ALL' ? transactions
    : transactions.filter(t => t.type === filter)

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-white">Transaction History</h1>
        <div className="flex gap-2">
          {['ALL', 'BUY', 'SELL'].map(f => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={clsx('text-sm px-3 py-1.5 rounded-lg transition-colors',
                filter === f ? 'bg-violet-600 text-white' : 'bg-slate-800 text-slate-400 hover:text-white'
              )}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-48">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="card text-center py-16">
          <ArrowLeftRight size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400">No transactions yet. Start trading!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {/* Desktop table */}
          <div className="card overflow-x-auto hidden sm:block">
            <table className="w-full text-sm">
              <thead className="text-left text-slate-400 border-b border-slate-800">
                <tr>
                  <th className="pb-3 pr-4">Type</th>
                  <th className="pb-3 pr-4">Symbol</th>
                  <th className="pb-3 pr-4">Company</th>
                  <th className="pb-3 pr-4">Qty</th>
                  <th className="pb-3 pr-4">Price</th>
                  <th className="pb-3 pr-4">Total</th>
                  <th className="pb-3 pr-4">Balance After</th>
                  <th className="pb-3">Date</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(tx => (
                  <tr key={tx.id} className="border-b border-slate-800 last:border-0 hover:bg-slate-800/30 transition-colors">
                    <td className="py-3 pr-4">
                      <span className={clsx(
                        'flex items-center gap-1.5 text-xs font-semibold px-2 py-1 rounded w-fit',
                        tx.type === 'BUY'
                          ? 'bg-emerald-500/20 text-emerald-400'
                          : 'bg-red-500/20 text-red-400'
                      )}>
                        {tx.type === 'BUY'
                          ? <ArrowUpRight size={12} />
                          : <ArrowDownRight size={12} />}
                        {tx.type}
                      </span>
                    </td>
                    <td className="py-3 pr-4 font-semibold text-white">{tx.symbol}</td>
                    <td className="py-3 pr-4 text-slate-400 max-w-[150px] truncate">{tx.companyName}</td>
                    <td className="py-3 pr-4 text-slate-300">{tx.quantity}</td>
                    <td className="py-3 pr-4 text-slate-300">${parseFloat(tx.pricePerShare || 0).toFixed(2)}</td>
                    <td className="py-3 pr-4 font-semibold text-white">${parseFloat(tx.totalAmount || 0).toFixed(2)}</td>
                    <td className="py-3 pr-4 text-slate-300">${parseFloat(tx.balanceAfter || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}</td>
                    <td className="py-3 text-slate-400 text-xs">
                      {tx.transactionDate ? new Date(tx.transactionDate).toLocaleString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Mobile card list */}
          <div className="sm:hidden space-y-2">
            {filtered.map(tx => (
              <div key={tx.id} className="card flex items-center gap-3">
                <span className={clsx(
                  'flex items-center justify-center w-9 h-9 rounded-xl flex-shrink-0',
                  tx.type === 'BUY' ? 'bg-emerald-500/20' : 'bg-red-500/20'
                )}>
                  {tx.type === 'BUY'
                    ? <ArrowUpRight size={16} className="text-emerald-400" />
                    : <ArrowDownRight size={16} className="text-red-400" />}
                </span>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <span className="font-semibold text-white">{tx.symbol}</span>
                    <span className="font-semibold text-white text-sm">${parseFloat(tx.totalAmount || 0).toFixed(2)}</span>
                  </div>
                  <div className="flex items-center justify-between mt-0.5">
                    <span className="text-xs text-slate-400">{tx.quantity} × ${parseFloat(tx.pricePerShare || 0).toFixed(2)}</span>
                    <span className="text-xs text-slate-500">
                      {tx.transactionDate ? new Date(tx.transactionDate).toLocaleDateString() : '—'}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
