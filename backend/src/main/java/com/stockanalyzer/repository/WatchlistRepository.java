package com.stockanalyzer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stockanalyzer.entity.User;
import com.stockanalyzer.entity.Watchlist;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    /** Eagerly fetch stocks in the same query to avoid LazyInitializationException. */
    @Query("SELECT DISTINCT w FROM Watchlist w LEFT JOIN FETCH w.stocks WHERE w.user = :user")
    List<Watchlist> findByUserWithStocks(@Param("user") User user);

    /** Eagerly fetch stocks for a single watchlist. */
    @Query("SELECT w FROM Watchlist w LEFT JOIN FETCH w.stocks WHERE w.id = :id AND w.user = :user")
    Optional<Watchlist> findByIdAndUserWithStocks(@Param("id") Long id, @Param("user") User user);

    List<Watchlist> findByUser(User user);

    Optional<Watchlist> findByIdAndUser(Long id, User user);

    boolean existsByUserAndName(User user, String name);
}
