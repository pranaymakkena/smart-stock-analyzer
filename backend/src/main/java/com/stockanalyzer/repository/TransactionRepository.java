package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Transaction;
import com.stockanalyzer.entity.User;
import com.stockanalyzer.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    List<Transaction> findByUserAndTypeOrderByTransactionDateDesc(User user, TransactionType type);

    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateDesc(
        User user, LocalDateTime from, LocalDateTime to);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentByUser(User user);

    long countByUser(User user);
}
