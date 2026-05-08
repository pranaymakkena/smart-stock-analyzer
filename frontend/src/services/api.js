import axios from 'axios'
import toast from 'react-hot-toast'

// In production (Vercel), VITE_API_URL = https://your-app.onrender.com
// In local dev, requests go to /api and the Vite proxy forwards to localhost:8080
const BASE_URL = import.meta.env.VITE_API_URL
  ? `${import.meta.env.VITE_API_URL}/api`
  : '/api'

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// Attach JWT token to every request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Handle 401 — try refresh token
api.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config
    if (err.response?.status === 401 && !original._retry) {
      original._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post('/api/auth/refresh', null, {
            params: { refreshToken }
          })
          const newToken = res.data.data.accessToken
          localStorage.setItem('accessToken', newToken)
          original.headers.Authorization = `Bearer ${newToken}`
          return api(original)
        } catch {
          localStorage.clear()
          window.location.href = '/login'
        }
      }
    }
    const message = err.response?.data?.message || 'Something went wrong'
    if (err.response?.status !== 401) toast.error(message)
    return Promise.reject(err)
  }
)

export default api

// ─── Stock APIs ──────────────────────────────────────────────────────────────
export const stockApi = {
  getAll:          ()         => api.get('/stocks'),
  getBySymbol:     (symbol)   => api.get(`/stocks/${symbol}`),
  search:          (q)        => api.get('/stocks/search', { params: { q } }),
  getBySector:     (sector)   => api.get(`/stocks/sector/${sector}`),
  getMarketSummary:()         => api.get('/stocks/market/summary'),
  getHistory:      (sym, per) => api.get(`/stocks/${sym}/history`, { params: { period: per } }),
  getSectors:      ()         => api.get('/stocks/market/sectors'),
  getGainers:      (n=10)     => api.get('/stocks/market/gainers', { params: { limit: n } }),
  getLosers:       (n=10)     => api.get('/stocks/market/losers',  { params: { limit: n } }),
  getMostActive:   (n=10)     => api.get('/stocks/market/active',  { params: { limit: n } }),
}

// ─── Portfolio APIs ───────────────────────────────────────────────────────────
export const portfolioApi = {
  getAll:       ()       => api.get('/portfolio'),
  getById:      (id)     => api.get(`/portfolio/${id}`),
  create:       (name)   => api.post('/portfolio', null, { params: { name } }),
  buy:          (data)   => api.post('/portfolio/buy', data),
  sell:         (data)   => api.post('/portfolio/sell', data),
  transactions: ()       => api.get('/portfolio/transactions'),
  refresh:      ()       => api.post('/portfolio/refresh'),
  delete:       (id)     => api.delete(`/portfolio/${id}`),
}

// ─── Prediction APIs ──────────────────────────────────────────────────────────
export const predictionApi = {
  predict:      (sym, strategy='moving-average') => api.get(`/predictions/${sym}`, { params: { strategy } }),
  predictAll:   (sym)    => api.get(`/predictions/${sym}/all`),
  risk:         (sym)    => api.get(`/predictions/${sym}/risk`),
  recommendations: ()   => api.get('/predictions/recommendations'),
  indicators:   (sym)    => api.get(`/predictions/${sym}/indicators`),
}

// ─── Watchlist APIs ───────────────────────────────────────────────────────────
export const watchlistApi = {
  getAll:       ()              => api.get('/watchlist'),
  create:       (name)          => api.post('/watchlist', null, { params: { name } }),
  addStock:     (id, symbol)    => api.post(`/watchlist/${id}/stocks`, null, { params: { symbol } }),
  removeStock:  (id, symbol)    => api.delete(`/watchlist/${id}/stocks/${symbol}`),
  getStocks:    (id)            => api.get(`/watchlist/${id}/stocks`),
  delete:       (id)            => api.delete(`/watchlist/${id}`),
}

// ─── Alert APIs ───────────────────────────────────────────────────────────────
export const alertApi = {
  getAll:   ()       => api.get('/alerts'),
  create:   (data)   => api.post('/alerts', data),
  delete:   (id)     => api.delete(`/alerts/${id}`),
  toggle:   (id)     => api.patch(`/alerts/${id}/toggle`),
}

// ─── Admin APIs ───────────────────────────────────────────────────────────────
export const adminApi = {
  getUsers:      () => api.get('/admin/users'),
  getLeaderboard:() => api.get('/admin/leaderboard'),
  getStats:      () => api.get('/admin/stats'),
}
