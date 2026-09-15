package com.frauddetectionsystem.repository;


import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTimestampDesc(User user);
    List<Transaction> findByUserIdOrderByTimestampDesc(Long userId);
    List<Transaction> findByUserAndTimestampAfter(User user, LocalDateTime after);
    List<Transaction> findByRiskLevel(Transaction.RiskLevel riskLevel);
    List<Transaction> findAllByOrderByTimestampDesc();
}