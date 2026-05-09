import React, { useEffect, useState } from 'react'
import { alertApi } from '../services/api'
import Modal from '../components/Modal'
import { Bell, Plus, Trash2, ToggleLeft, ToggleRight, CheckCircle } from 'lucide-react'
import toast from 'react-hot-toast'
import clsx from 'clsx'

const ALERT_TYPES = ['PRICE_ABOVE', 'PRICE_BELOW', 'VOLUME_SPIKE', 'MARKET_DROP', 'PERCENT_CHANGE']

const EMPTY_FORM = { symbol: '', alertType: 'PRICE_ABOVE', targetPrice: '', targetPercent: '', message: '' }

export default function AlertsPage() {
  const [alerts, setAlerts]     = useState([])
  const [loading, setLoading]   = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm]         = useState(EMPTY_FORM)

  useEffect(() => { loadAlerts() }, [])

  const loadAlerts = () => {
    alertApi.getAll()
      .then(res => setAlerts(res.data.data || []))
      .finally(() => setLoading(false))
  }

  const handleCreate = async e => {
    e.preventDefault()
    try {
      await alertApi.create({
        ...form,
        targetPrice:   form.targetPrice   ? parseFloat(form.targetPrice)   : null,
        targetPercent: form.targetPercent ? parseFloat(form.targetPercent) : null,
      })
      toast.success('Alert created')
      setShowForm(false)
      setForm(EMPTY_FORM)
      loadAlerts()
    } catch {}
  }

  const handleDelete = async (id) => {
    try {
      await alertApi.delete(id)
      toast.success('Alert deleted')
      loadAlerts()
    } catch {}
  }

  const handleToggle = async (id) => {
    try {
      await alertApi.toggle(id)
      loadAlerts()
    } catch {}
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-white">Smart Alerts</h1>
        <button
          onClick={() => { setForm(EMPTY_FORM); setShowForm(true) }}
          className="btn-primary text-sm flex items-center gap-2"
        >
          <Plus size={14} /> New Alert
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-48">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-violet-500" />
        </div>
      ) : alerts.length === 0 ? (
        <div className="card text-center py-16">
          <Bell size={48} className="mx-auto text-slate-600 mb-4" />
          <p className="text-slate-400">No alerts set. Create one to get notified!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {alerts.map(alert => (
            <div key={alert.id} className={clsx(
              'card flex items-start sm:items-center justify-between gap-4',
              alert.triggered ? 'border-emerald-800/50 bg-emerald-900/10' :
              !alert.active   ? 'opacity-60' : ''
            )}>
              <div className="flex items-start gap-3 min-w-0">
                {alert.triggered
                  ? <CheckCircle size={20} className="text-emerald-400 flex-shrink-0 mt-0.5" />
                  : <Bell size={20} className={clsx('flex-shrink-0 mt-0.5', alert.active ? 'text-violet-400' : 'text-slate-500')} />
                }
                <div className="min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-semibold text-white">{alert.stock?.symbol}</span>
                    <span className="text-xs bg-slate-800 text-slate-400 px-2 py-0.5 rounded">
                      {alert.alertType?.replace(/_/g, ' ')}
                    </span>
                    {alert.triggered && (
                      <span className="text-xs bg-emerald-500/20 text-emerald-400 px-2 py-0.5 rounded">TRIGGERED</span>
                    )}
                  </div>
                  <p className="text-sm text-slate-400 mt-0.5 truncate">
                    {alert.targetPrice   && `Target: $${parseFloat(alert.targetPrice).toFixed(2)}`}
                    {alert.targetPercent && `Target: ${alert.targetPercent}%`}
                    {alert.message       && ` — ${alert.message}`}
                  </p>
                  {alert.triggeredAt && (
                    <p className="text-xs text-emerald-400 mt-0.5">
                      Triggered: {new Date(alert.triggeredAt).toLocaleString()}
                    </p>
                  )}
                </div>
              </div>
              <div className="flex items-center gap-2 flex-shrink-0">
                {!alert.triggered && (
                  <button onClick={() => handleToggle(alert.id)} className="text-slate-400 hover:text-violet-400 transition-colors">
                    {alert.active
                      ? <ToggleRight size={20} className="text-violet-400" />
                      : <ToggleLeft size={20} />
                    }
                  </button>
                )}
                <button onClick={() => handleDelete(alert.id)} className="text-slate-500 hover:text-red-400 transition-colors">
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Alert Modal */}
      <Modal open={showForm} onClose={() => setShowForm(false)} title="Create Alert" maxWidth="max-w-lg">
        <form onSubmit={handleCreate} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs text-slate-400 mb-1">Stock Symbol</label>
              <input
                className="input"
                placeholder="AAPL"
                value={form.symbol}
                onChange={e => setForm({ ...form, symbol: e.target.value.toUpperCase() })}
                required
              />
            </div>
            <div>
              <label className="block text-xs text-slate-400 mb-1">Alert Type</label>
              <select
                className="input"
                value={form.alertType}
                onChange={e => setForm({ ...form, alertType: e.target.value })}
              >
                {ALERT_TYPES.map(t => (
                  <option key={t} value={t}>{t.replace(/_/g, ' ')}</option>
                ))}
              </select>
            </div>

            {(form.alertType === 'PRICE_ABOVE' || form.alertType === 'PRICE_BELOW') && (
              <div>
                <label className="block text-xs text-slate-400 mb-1">Target Price ($)</label>
                <input
                  type="number" step="0.01" className="input" placeholder="150.00"
                  value={form.targetPrice}
                  onChange={e => setForm({ ...form, targetPrice: e.target.value })}
                />
              </div>
            )}

            {form.alertType === 'PERCENT_CHANGE' && (
              <div>
                <label className="block text-xs text-slate-400 mb-1">Target % Change</label>
                <input
                  type="number" step="0.1" className="input" placeholder="5.0"
                  value={form.targetPercent}
                  onChange={e => setForm({ ...form, targetPercent: e.target.value })}
                />
              </div>
            )}

            <div className="sm:col-span-2">
              <label className="block text-xs text-slate-400 mb-1">Custom Message (optional)</label>
              <input
                className="input"
                placeholder="Alert message..."
                value={form.message}
                onChange={e => setForm({ ...form, message: e.target.value })}
              />
            </div>
          </div>

          <div className="flex gap-3 pt-1">
            <button type="submit" className="btn-primary flex-1 sm:flex-none">Create Alert</button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary flex-1 sm:flex-none">Cancel</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
