# 📈 Smart Stock Market Analyzer

A full-stack stock market analytics platform built with **Spring Boot** + **React**.

## 🚀 Features

| Feature | Description |
|---------|-------------|
| 📊 Live Dashboard | Real-time stock prices, gainers, losers, market summary |
| 📈 Interactive Charts | Area charts with daily/weekly/monthly/yearly periods |
| 🧠 Price Prediction | Moving Average, Linear Regression, Trend Analysis (MACD + Bollinger) |
| 💼 Portfolio Management | Virtual buy/sell with P&L tracking |
| ⭐ Watchlists | Save and monitor favorite stocks |
| 🚨 Smart Alerts | Price above/below, volume spike, market drop notifications |
| 📉 Risk Analysis | Volatility, Sharpe ratio, max drawdown, risk score |
| 🔍 Stock Search | Search by symbol or company name |
| 🌍 Sector Analytics | Compare IT, Banking, Pharma, Energy performance |
| 🏆 Leaderboard | Virtual trading competition rankings |
| 🔐 Role-Based Access | Admin, Investor, Analyst roles |
| 🔄 WebSocket | Real-time price updates via STOMP/SockJS |

## 🧱 Tech Stack

### Backend
- Java 17 + Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA + H2 (dev) / PostgreSQL (prod)
- WebSocket (STOMP)
- Apache Commons Math (Linear Regression)
- iText PDF (report export)

### Frontend
- React 18 + Vite
- Tailwind CSS (dark theme)
- Recharts (interactive charts)
- React Router v6
- Axios + JWT interceptors

## 🧠 OOP Concepts Demonstrated

| Concept | Implementation |
|---------|---------------|
| **Encapsulation** | `User`, `Stock`, `Portfolio`, `Transaction` entities |
| **Abstraction** | `StockService`, `PredictionService`, `PortfolioService` interfaces |
| **Inheritance** | `BaseUser` → `User` (Investor/Admin/Analyst) |
| **Polymorphism** | `PredictionStrategy` → `MovingAveragePrediction`, `LinearRegressionPrediction`, `TrendAnalysisPrediction` |

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
Backend runs at: http://localhost:8080  
H2 Console: http://localhost:8080/h2-console

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs at: http://localhost:3000

---

## ☁️ Deployment

### Backend → Render (Docker)

1. Push this repo to GitHub
2. Go to [render.com](https://render.com) → **New → Web Service**
3. Connect your GitHub repo, set **Root Directory** to `backend`
4. Choose **Docker** as the runtime — Render will use `backend/Dockerfile`
5. Add a **PostgreSQL** database: Render → New → PostgreSQL, then copy the **Internal Database URL**
6. Set these **Environment Variables** in Render:

| Variable | Value |
|----------|-------|
| `DATABASE_URL` | `jdbc:postgresql://...` (Render Postgres internal URL) |
| `DB_USERNAME` | your Postgres user |
| `DB_PASSWORD` | your Postgres password |
| `DB_DRIVER` | `org.postgresql.Driver` |
| `HIBERNATE_DIALECT` | `org.hibernate.dialect.PostgreSQLDialect` |
| `DDL_AUTO` | `update` |
| `JWT_SECRET` | any long random string (32+ chars) |
| `ALLOWED_ORIGINS` | `https://your-app.vercel.app` |
| `ENABLE_H2_CONSOLE` | `false` |

> Render free tier spins down after inactivity — first request may take ~30s to wake.

### Frontend → Vercel

1. Go to [vercel.com](https://vercel.com) → **New Project** → import your GitHub repo
2. Set **Root Directory** to `frontend`
3. Framework preset: **Vite**
4. Add this **Environment Variable**:

| Variable | Value |
|----------|-------|
| `VITE_API_URL` | `https://your-backend.onrender.com` |

5. Deploy — `vercel.json` handles React Router client-side routing automatically.

---

## 👤 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@stockanalyzer.com | admin123 |
| Investor | investor@stockanalyzer.com | investor123 |
| Analyst | analyst@stockanalyzer.com | analyst123 |

## 📁 Project Structure

```
smart-stock-analyzer/
├── backend/
│   └── src/main/java/com/stockanalyzer/
│       ├── controller/          # REST endpoints
│       ├── service/             # Business logic interfaces (Abstraction)
│       │   └── impl/            # Service implementations
│       ├── entity/              # JPA entities (Encapsulation)
│       │   └── enums/           # Role, Sector, AlertType, TransactionType
│       ├── repository/          # Spring Data JPA repositories
│       ├── strategy/            # Prediction strategies (Polymorphism)
│       │   ├── PredictionStrategy.java
│       │   ├── MovingAveragePrediction.java
│       │   ├── LinearRegressionPrediction.java
│       │   └── TrendAnalysisPrediction.java
│       ├── security/            # JWT + Spring Security
│       ├── dto/                 # Request/Response DTOs
│       ├── config/              # DataSeeder, WebSocket, Scheduler
│       └── exception/           # Global exception handler
└── frontend/
    └── src/
        ├── pages/               # Dashboard, Market, Portfolio, etc.
        ├── components/          # Layout, StockCard, StockChart
        ├── context/             # AuthContext
        └── services/            # API layer (axios)
```

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| GET | `/api/stocks` | All stocks |
| GET | `/api/stocks/{symbol}` | Stock details |
| GET | `/api/stocks/market/summary` | Market overview |
| GET | `/api/stocks/{symbol}/history?period=1m` | Price history |
| GET | `/api/predictions/{symbol}/all` | All 3 predictions |
| GET | `/api/predictions/{symbol}/risk` | Risk analysis |
| GET | `/api/portfolio` | User portfolios |
| POST | `/api/portfolio/buy` | Buy stock |
| POST | `/api/portfolio/sell` | Sell stock |
| GET | `/api/watchlist` | User watchlists |
| POST | `/api/alerts` | Create alert |
| GET | `/api/admin/leaderboard` | Rankings (Admin only) |

## 🔄 Switch to PostgreSQL

In `application.properties`, comment out H2 and uncomment:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stockanalyzer
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## 📄 Resume Title
> **Smart Stock Market Analyzer** with Trend Prediction, Portfolio Tracking, and Real-Time Analytics using Spring Boot & React
