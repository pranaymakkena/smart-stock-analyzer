import React, { useEffect, useRef } from 'react'
import { X } from 'lucide-react'

/**
 * Reusable modal dialog.
 *
 * Props:
 *   open       – boolean, controls visibility
 *   onClose    – called when backdrop or X is clicked
 *   title      – string, modal heading
 *   children   – modal body content
 *   maxWidth   – tailwind max-w class, default 'max-w-md'
 */
export default function Modal({ open, onClose, title, children, maxWidth = 'max-w-md' }) {
  const overlayRef = useRef(null)

  // Close on Escape key
  useEffect(() => {
    if (!open) return
    const handler = (e) => { if (e.key === 'Escape') onClose() }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [open, onClose])

  // Prevent body scroll when open
  useEffect(() => {
    document.body.style.overflow = open ? 'hidden' : ''
    return () => { document.body.style.overflow = '' }
  }, [open])

  if (!open) return null

  return (
    <div
      ref={overlayRef}
      className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4"
      style={{ background: 'rgba(0,0,0,0.65)', backdropFilter: 'blur(4px)' }}
      onClick={e => { if (e.target === overlayRef.current) onClose() }}
    >
      <div
        className={`relative w-full ${maxWidth} rounded-t-2xl sm:rounded-2xl animate-slide-up`}
        style={{ background: '#13161e', border: '1px solid #1f2433', maxHeight: '90vh', overflowY: 'auto' }}
      >
        {/* Drag handle (mobile) */}
        <div className="flex justify-center pt-3 sm:hidden">
          <div className="w-10 h-1 rounded-full" style={{ background: '#2d3348' }} />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4" style={{ borderBottom: '1px solid #1f2433' }}>
          <h2 className="text-base font-semibold text-white">{title}</h2>
          <button
            onClick={onClose}
            className="w-7 h-7 flex items-center justify-center rounded-lg transition-colors"
            style={{ color: '#8892a4' }}
            onMouseEnter={e => e.currentTarget.style.color = '#e2e8f0'}
            onMouseLeave={e => e.currentTarget.style.color = '#8892a4'}
          >
            <X size={16} />
          </button>
        </div>

        {/* Body */}
        <div className="px-5 py-5">
          {children}
        </div>
      </div>
    </div>
  )
}

/**
 * Simple confirmation modal.
 *
 * Props:
 *   open, onClose, onConfirm, title, message,
 *   confirmLabel (default 'Confirm'), danger (bool)
 */
export function ConfirmModal({ open, onClose, onConfirm, title = 'Are you sure?', message, confirmLabel = 'Confirm', danger = false }) {
  return (
    <Modal open={open} onClose={onClose} title={title} maxWidth="max-w-sm">
      {message && <p className="text-sm text-slate-400 mb-6">{message}</p>}
      <div className="flex gap-3 justify-end">
        <button onClick={onClose} className="btn-secondary text-sm">Cancel</button>
        <button
          onClick={() => { onConfirm(); onClose() }}
          className={danger ? 'btn-danger text-sm' : 'btn-primary text-sm'}
        >
          {confirmLabel}
        </button>
      </div>
    </Modal>
  )
}

/**
 * Single text-input prompt modal.
 *
 * Props:
 *   open, onClose, onSubmit(value), title, label, placeholder, submitLabel
 */
export function PromptModal({ open, onClose, onSubmit, title, label, placeholder = '', submitLabel = 'Create' }) {
  const [value, setValue] = React.useState('')

  // Reset on open
  useEffect(() => { if (open) setValue('') }, [open])

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!value.trim()) return
    onSubmit(value.trim())
    onClose()
  }

  return (
    <Modal open={open} onClose={onClose} title={title} maxWidth="max-w-sm">
      <form onSubmit={handleSubmit} className="space-y-4">
        {label && <label className="block text-xs text-slate-400 mb-1">{label}</label>}
        <input
          autoFocus
          className="input"
          placeholder={placeholder}
          value={value}
          onChange={e => setValue(e.target.value)}
        />
        <div className="flex gap-3 justify-end">
          <button type="button" onClick={onClose} className="btn-secondary text-sm">Cancel</button>
          <button type="submit" disabled={!value.trim()} className="btn-primary text-sm">{submitLabel}</button>
        </div>
      </form>
    </Modal>
  )
}
