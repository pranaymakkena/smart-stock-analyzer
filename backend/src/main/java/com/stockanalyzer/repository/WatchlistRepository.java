package com.stockanalyzer.repository;

import com.stockanalyzer.entity.User;
import com.stockanalyzer.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUser(User user);

    Optional<Watchlist> findByIdAndUser(Long id, User user);

    boolean existsByUserAndName(User user, String name);
}
