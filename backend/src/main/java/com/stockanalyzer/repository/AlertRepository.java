package com.stockanalyzer.repository;

import com.stockanalyzer.entity.Alert;
import com.stockanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUser(User user);

    List<Alert> findByUserAndActive(User user, boolean active);

    @Query("SELECT a FROM Alert a WHERE a.active = true AND a.triggered = false")
    List<Alert> findAllActiveUntriggered();

    List<Alert> findByUserOrderByCreatedAtDesc(User user);
}
