package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Prediction;
import com.stockanalyzer.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findByStockOrderByCreatedAtDesc(Stock stock);

    Optional<Prediction> findTopByStockOrderByCreatedAtDesc(Stock stock);

    List<Prediction> findByStockAndStrategyUsedOrderByCreatedAtDesc(Stock stock, String strategy);
}
