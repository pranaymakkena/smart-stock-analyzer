# 📈 Smart Stock Market Analyzer

A full-stack stock market analytics platform built with **Spring Boot** + **React**.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📊 Live Dashboard | Real-time stock prices, top gainers/losers, market summary |
| 📈 Interactive Charts | Area charts with 1W / 1M / 3M / 6M / 1Y periods + volume bars |
| 🧠 Price Prediction | Moving Average, Linear Regression, Trend Analysis (MACD + Bollinger Bands) |
| 💼 Portfolio Management | Virtual buy/sell with avg cost, P&L, and profit % tracking |
| ⭐ Watchlists | Create multiple watchlists, add/remove stocks |
| 🚨 Smart Alerts | Notify when price crosses target, volume spikes, or market drops |
| 📉 Risk Analysis | Volatility, Sharpe ratio, max drawdown, risk score 1–10 |
| 🔍 Stock Search | Search by symbol or company name with sector filter |
| 🌍 Sector Analytics | Compare IT, Banking, Pharma, Energy, Automotive performance |
| 🏆 Leaderboard | Virtual trading competition ranked by profit (Admin only) |
| 🔐 Role-Based Access | Admin, Investor, Analyst roles with JWT auth |
| 🔄 WebSocket | Real-time price updates via STOMP/SockJS |

---

## 🧱 Tech Stack

### Backend
- **Java 21** + Spring Boot 3.2
- Spring Security + JWT (jjwt 0.11)
- Spring Data JPA — H2 (local dev) / PostgreSQL (production)
- WebSocket (STOMP + SockJS)
- Apache Commons Math 3 (Linear Regression)
- Spring Boot WebFlux (reactive HTTP client)

### Frontend
- **React 18** + Vite 5
- Tailwind CSS — dark theme with Inter font
- Recharts — area charts, bar charts, pie charts
- React Router v6
- Axios with JWT interceptor + auto token refresh

---

## 🧠 OOP Concepts Demonstrated

| Concept | Where |
|---------|-------|
| **Encapsulation** | `User`, `Stock`, `Portfolio`, `Transaction`, `Alert` entities |
| **Abstraction** | `StockService`, `PredictionService`, `PortfolioService` interfaces |
| **Inheritance** | `BaseUser` → `User` (role: INVESTOR / ADMIN / ANALYST) |
| **Polymorphism** | `PredictionStrategy` → `MovingAveragePrediction`, `LinearRegressionPrediction`, `TrendAnalysisPrediction` |

---

## 🚀 Quick Start (Local)

### Prerequisites
- Java 21+
- Node.js 18+
- Maven 3.8+

### Backend
```bash
cd backend
mvn spring-boot:run
```

- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
- On first run, 20 stocks + 365 days of price history are seeded automatically

### Frontend
```bash
cd frontend
npm install
npm run dev
```

- App: http://localhost:3000

### Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@stockanalyzer.com | admin123 |
| Investor | investor@stockanalyzer.com | investor123 |
| Analyst | analyst@stockanalyzer.com | analyst123 |

---

## ☁️ Deployment

### Backend → Render

1. Push this repo to GitHub
2. [render.com](https://render.com) → **New → PostgreSQL**
   - Name it anything (e.g. `stockanalyzer-db`) — this is just a display label
   - After creation, copy the **Internal Database URL** (starts with `postgres://...`)
3. **New → Web Service** → connect your GitHub repo
   - **Root Directory:** `backend`
   - **Runtime:** Docker ← Render auto-uses `backend/Dockerfile`
   - **Branch:** `main`
4. Under **Environment Variables**, add:

| Key | Value |
|-----|-------|
| `SPRING_DATASOURCE_URL` | Render's Internal DB URL with `postgres://` changed to `jdbc:postgresql://` — e.g. `jdbc:postgresql://user:pass@host:5432/db` |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `org.postgresql.Driver` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `JWT_SECRET` | any random string, 32+ chars |
| `ALLOWED_ORIGINS` | your Vercel URL, e.g. `https://your-app.vercel.app` |
| `ENABLE_H2_CONSOLE` | `false` |
| `LOG_LEVEL` | `INFO` |

5. Click **Create Web Service** — first build takes ~5–8 min
6. Copy your service URL: `https://your-app.onrender.com`

> **Note:** Render's free tier sleeps after 15 min of inactivity. The first request after sleep takes ~30s to wake up.

---

### Frontend → Vercel

1. [vercel.com](https://vercel.com) → **New Project** → import your GitHub repo
2. Configure:
   - **Root Directory:** `frontend`
   - **Framework Preset:** Vite
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
3. Under **Environment Variables**, add:

| Key | Value |
|-----|-------|
| `VITE_API_URL` | `https://your-app.onrender.com` (your Render URL, no trailing slash) |

4. Click **Deploy** — takes ~1–2 min
5. Go back to Render → update `ALLOWED_ORIGINS` to your Vercel URL → **Save** (triggers redeploy)

> `vercel.json` is already configured to handle React Router — all routes redirect to `index.html`.

---

## 📁 Project Structure

```
smart-stock-analyzer/
├── backend/
│   ├── Dockerfile
│   ├── .env.example                  ← copy to Render env vars
│   └── src/main/java/com/stockanalyzer/
│       ├── SmartStockAnalyzerApplication.java
│       ├── config/
│       │   ├── DataSourceConfig.java ← handles postgres:// → jdbc:postgresql://
│       │   ├── DataSeeder.java       ← seeds 20 stocks + 365d history on startup
│       │   ├── StockPriceScheduler.java
│       │   └── WebSocketConfig.java
│       ├── controller/               ← REST endpoints
│       ├── service/                  ← interfaces (Abstraction)
│       │   └── impl/                 ← implementations
│       ├── entity/                   ← JPA entities (Encapsulation)
│       │   └── enums/
│       ├── repository/               ← Spring Data JPA
│       ├── strategy/                 ← Prediction strategies (Polymorphism)
│       │   ├── PredictionStrategy.java
│       │   ├── MovingAveragePrediction.java
│       │   ├── LinearRegressionPrediction.java
│       │   └── TrendAnalysisPrediction.java
│       ├── security/                 ← JWT filter, SecurityConfig
│       ├── dto/                      ← Request / Response DTOs
│       └── exception/                ← GlobalExceptionHandler
└── frontend/
    ├── vercel.json                   ← SPA routing fix for Vercel
    ├── .env.example                  ← VITE_API_URL
    └── src/
        ├── App.jsx
        ├── pages/
        │   ├── DashboardPage.jsx
        │   ├── MarketPage.jsx
        │   ├── StockDetailPage.jsx
        │   ├── PortfolioPage.jsx
        │   ├── PredictionPage.jsx
        │   ├── WatchlistPage.jsx
        │   ├── AlertsPage.jsx
        │   ├── TransactionsPage.jsx
        │   └── LeaderboardPage.jsx
        ├── components/
        │   ├── Layout.jsx
        │   ├── StockCard.jsx
        │   └── StockChart.jsx
        ├── context/AuthContext.jsx
        └── services/api.js
```

---

## 🔌 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | — | Register new user |
| POST | `/api/auth/login` | — | Login, returns JWT |
| GET | `/api/auth/me` | ✓ | Current user info |
| GET | `/api/stocks` | ✓ | All tracked stocks |
| GET | `/api/stocks/{symbol}` | ✓ | Stock detail |
| GET | `/api/stocks/search?q=` | ✓ | Search stocks |
| GET | `/api/stocks/market/summary` | ✓ | Gainers, losers, active |
| GET | `/api/stocks/{symbol}/history?period=1m` | ✓ | OHLCV history |
| GET | `/api/stocks/market/sectors` | ✓ | Sector performance |
| GET | `/api/predictions/{symbol}/all` | ✓ | All 3 strategy predictions |
| GET | `/api/predictions/{symbol}/risk` | ✓ | Risk analysis |
| GET | `/api/predictions/recommendations` | ✓ | AI-like insights |
| GET | `/api/portfolio` | ✓ | User portfolios |
| POST | `/api/portfolio/buy` | ✓ | Buy stock |
| POST | `/api/portfolio/sell` | ✓ | Sell stock |
| GET | `/api/portfolio/transactions` | ✓ | Transaction history |
| GET | `/api/watchlist` | ✓ | User watchlists |
| POST | `/api/watchlist/{id}/stocks` | ✓ | Add stock to watchlist |
| GET | `/api/alerts` | ✓ | User alerts |
| POST | `/api/alerts` | ✓ | Create alert |
| GET | `/api/admin/leaderboard` | Admin | Rankings by profit |
| GET | `/api/admin/users` | Admin | All users |

---

## 📄 Resume Title

> **Smart Stock Market Analyzer** with Trend Prediction, Portfolio Tracking, and Real-Time Analytics using Spring Boot & React
