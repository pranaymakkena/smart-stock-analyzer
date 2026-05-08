package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Stock;
import com.stockanalyzer.entity.enums.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    List<Stock> findBySector(Sector sector);

    @Query("SELECT s FROM Stock s WHERE UPPER(s.symbol) LIKE UPPER(CONCAT('%', :query, '%')) " +
           "OR UPPER(s.companyName) LIKE UPPER(CONCAT('%', :query, '%'))")
    List<Stock> searchStocks(String query);

    @Query("SELECT s FROM Stock s ORDER BY s.changePercent DESC")
    List<Stock> findTopGainers();

    @Query("SELECT s FROM Stock s ORDER BY s.changePercent ASC")
    List<Stock> findTopLosers();

    @Query("SELECT s FROM Stock s ORDER BY s.volume DESC")
    List<Stock> findMostActive();

    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL")
    List<Sector> findAllSectors();
}
