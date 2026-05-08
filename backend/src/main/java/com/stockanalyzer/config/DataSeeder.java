package com.stockanalyzer.config;

import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.StockHistory;
import com.stockanalyzer.entity.User;
import com.stockanalyzer.entity.enums.Role;
import com.stockanalyzer.entity.enums.Sector;
import com.stockanalyzer.repository.StockHistoryRepository;
import com.stockanalyzer.repository.StockRepository;
import com.stockanalyzer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeds the database with realistic demo data on startup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final StockHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, StockRepository stockRepository,
                      StockHistoryRepository historyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.historyRepository = historyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedStocks();
        log.info("✅ Data seeding complete. App ready at http://localhost:8080");
        log.info("📊 H2 Console: http://localhost:8080/h2-console");
        log.info("👤 Admin: admin@stockanalyzer.com / admin123");
        log.info("👤 Investor: investor@stockanalyzer.com / investor123");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.save(new User("Admin", "User",
            "admin@stockanalyzer.com", passwordEncoder.encode("admin123"), Role.ADMIN));
        userRepository.save(new User("John", "Investor",
            "investor@stockanalyzer.com", passwordEncoder.encode("investor123"), Role.INVESTOR));
        userRepository.save(new User("Sarah", "Analyst",
            "analyst@stockanalyzer.com", passwordEncoder.encode("analyst123"), Role.ANALYST));
        userRepository.save(new User("Mike", "Trader",
            "trader@stockanalyzer.com", passwordEncoder.encode("trader123"), Role.INVESTOR));

        log.info("✅ Users seeded");
    }

    private void seedStocks() {
        if (stockRepository.count() > 0) return;

        List<Object[]> stockData = List.of(
            // symbol, company, sector, price, open, high, low, prevClose, changePct, volume, mktCap
            new Object[]{"AAPL",  "Apple Inc.",                  Sector.TECHNOLOGY,    189.50, 188.20, 191.30, 187.80, 187.90,  0.85, 55_000_000L,  2_950_000_000_000L},
            new Object[]{"MSFT",  "Microsoft Corporation",       Sector.TECHNOLOGY,    415.20, 412.00, 418.50, 410.80, 413.10,  0.51, 22_000_000L,  3_080_000_000_000L},
            new Object[]{"GOOGL", "Alphabet Inc.",               Sector.TECHNOLOGY,    175.80, 174.20, 177.40, 173.50, 174.60,  0.69, 18_000_000L,  2_200_000_000_000L},
            new Object[]{"AMZN",  "Amazon.com Inc.",             Sector.TECHNOLOGY,    185.40, 183.80, 187.20, 182.90, 184.20,  0.65, 32_000_000L,  1_920_000_000_000L},
            new Object[]{"TSLA",  "Tesla Inc.",                  Sector.AUTOMOTIVE,    245.60, 242.10, 249.80, 240.30, 243.50,  0.86, 95_000_000L,    780_000_000_000L},
            new Object[]{"NVDA",  "NVIDIA Corporation",          Sector.TECHNOLOGY,    875.40, 868.20, 882.10, 865.30, 870.80,  0.53, 42_000_000L,  2_150_000_000_000L},
            new Object[]{"META",  "Meta Platforms Inc.",         Sector.TECHNOLOGY,    512.30, 508.40, 516.70, 506.20, 509.80,  0.49, 15_000_000L,  1_310_000_000_000L},
            new Object[]{"JPM",   "JPMorgan Chase & Co.",        Sector.BANKING,       198.70, 196.50, 200.30, 195.80, 197.20,  0.76, 12_000_000L,    572_000_000_000L},
            new Object[]{"BAC",   "Bank of America Corp.",       Sector.BANKING,        38.90,  38.40,  39.50,  38.10,  38.60,  0.78, 45_000_000L,    305_000_000_000L},
            new Object[]{"JNJ",   "Johnson & Johnson",           Sector.PHARMA,        152.40, 151.20, 153.80, 150.60, 151.80,  0.39,  8_000_000L,    366_000_000_000L},
            new Object[]{"PFE",   "Pfizer Inc.",                 Sector.PHARMA,         27.80,  27.50,  28.20,  27.30,  27.60,  0.72, 35_000_000L,    157_000_000_000L},
            new Object[]{"XOM",   "Exxon Mobil Corporation",     Sector.ENERGY,        118.50, 117.20, 119.80, 116.90, 117.80,  0.59, 18_000_000L,    472_000_000_000L},
            new Object[]{"CVX",   "Chevron Corporation",         Sector.ENERGY,        158.30, 156.80, 159.70, 156.10, 157.40,  0.57, 10_000_000L,    290_000_000_000L},
            new Object[]{"WMT",   "Walmart Inc.",                Sector.RETAIL,         68.40,  67.80,  69.10,  67.50,  68.00,  0.59, 20_000_000L,    549_000_000_000L},
            new Object[]{"INFY",  "Infosys Limited",             Sector.TECHNOLOGY,     19.80,  19.60,  20.10,  19.40,  19.70,  0.51, 14_000_000L,     82_000_000_000L},
            new Object[]{"TCS",   "Tata Consultancy Services",   Sector.TECHNOLOGY,     42.50,  42.10,  43.00,  41.80,  42.20,  0.71,  6_000_000L,    153_000_000_000L},
            new Object[]{"RELIANCE","Reliance Industries",       Sector.ENERGY,         30.20,  29.90,  30.60,  29.70,  30.00,  0.67,  8_000_000L,    204_000_000_000L},
            new Object[]{"HDFC",  "HDFC Bank Limited",           Sector.BANKING,        66.80,  66.20,  67.50,  65.90,  66.40,  0.60,  5_000_000L,    124_000_000_000L},
            new Object[]{"NFLX",  "Netflix Inc.",                Sector.TECHNOLOGY,    628.40, 622.10, 633.80, 619.50, 624.20,  0.67,  5_000_000L,    271_000_000_000L},
            new Object[]{"DIS",   "The Walt Disney Company",     Sector.CONSUMER_GOODS, 112.30, 111.20, 113.50, 110.80, 111.80,  0.45, 12_000_000L,    205_000_000_000L}
        );

        Random rng = new Random(42);

        for (Object[] data : stockData) {
            Stock stock = new Stock((String) data[0], (String) data[1], (Sector) data[2]);
            double price = (double) data[3];
            stock.setCurrentPrice(bd(price));
            stock.setOpenPrice(bd((double) data[4]));
            stock.setDayHigh(bd((double) data[5]));
            stock.setDayLow(bd((double) data[6]));
            stock.setPreviousClose(bd((double) data[7]));
            double changePct = (double) data[8];
            stock.setChangePercent(bd(changePct));
            stock.setChange(bd(price * changePct / 100));
            stock.setVolume((Long) data[9]);
            stock.setAvgVolume((long) ((Long) data[9] * 0.9));
            stock.setMarketCap(bd((long) data[10]));
            stock.setExchange("NASDAQ");
            stock.setFiftyTwoWeekHigh(bd(price * 1.35));
            stock.setFiftyTwoWeekLow(bd(price * 0.65));
            stock.setPeRatio(bd(15 + rng.nextDouble() * 25));
            stock = stockRepository.save(stock);

            // Generate 365 days of history
            generateHistory(stock, price, rng);
        }

        log.info("✅ {} stocks seeded with 365 days of history", stockData.size());
    }

    private void generateHistory(Stock stock, double currentPrice, Random rng) {
        List<StockHistory> histories = new ArrayList<>();
        double price = currentPrice * (0.65 + rng.nextDouble() * 0.35); // start lower
        LocalDate today = LocalDate.now();

        for (int i = 365; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            if (historyRepository.existsByStockAndDate(stock, date)) continue;

            double dailyReturn = (rng.nextGaussian() * 0.015) + 0.0003; // slight upward drift
            price = price * (1 + dailyReturn);
            price = Math.max(price, 0.01);

            double open  = price * (1 + (rng.nextDouble() - 0.5) * 0.01);
            double high  = Math.max(open, price) * (1 + rng.nextDouble() * 0.015);
            double low   = Math.min(open, price) * (1 - rng.nextDouble() * 0.015);
            long volume  = (long) (stock.getVolume() * (0.5 + rng.nextDouble()));

            histories.add(new StockHistory(stock, date,
                bd(open), bd(high), bd(low), bd(price), volume));
        }
        historyRepository.saveAll(histories);
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal bd(long value) {
        return BigDecimal.valueOf(value);
    }
}
