package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Portfolio;
import com.stockanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUser(User user);

    Optional<Portfolio> findByIdAndUser(Long id, User user);

    boolean existsByUserAndName(User user, String name);
}
