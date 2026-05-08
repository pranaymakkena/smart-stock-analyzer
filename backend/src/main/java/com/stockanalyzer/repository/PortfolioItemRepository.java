package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Portfolio;
import com.stockanalyzer.entity.PortfolioItem;
import com.stockanalyzer.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    List<PortfolioItem> findByPortfolio(Portfolio portfolio);

    Optional<PortfolioItem> findByPortfolioAndStock(Portfolio portfolio, Stock stock);

    boolean existsByPortfolioAndStock(Portfolio portfolio, Stock stock);
}
