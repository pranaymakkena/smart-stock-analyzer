package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    List<StockHistory> findByStockOrderByDateAsc(Stock stock);

    List<StockHistory> findByStockAndDateBetweenOrderByDateAsc(Stock stock, LocalDate from, LocalDate to);

    @Query("SELECT sh FROM StockHistory sh WHERE sh.stock = :stock ORDER BY sh.date DESC")
    List<StockHistory> findRecentByStock(Stock stock);

    @Query("SELECT sh FROM StockHistory sh WHERE sh.stock.symbol = :symbol ORDER BY sh.date ASC")
    List<StockHistory> findBySymbolOrderByDateAsc(String symbol);

    boolean existsByStockAndDate(Stock stock, LocalDate date);
}
