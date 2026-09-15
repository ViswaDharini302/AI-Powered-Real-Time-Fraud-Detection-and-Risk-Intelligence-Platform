package com.frauddetectionsystem.controller;


import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.repository.TransactionRepository;
import com.frauddetectionsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        List<Transaction> all = transactionRepository.findAll();

        long total = all.size();
        long flagged = all.stream().filter(t -> t.getStatus() == Transaction.TransactionStatus.FLAGGED
                || t.getStatus() == Transaction.TransactionStatus.CONFIRMED_FRAUD).count();
        long highRisk = all.stream().filter(t -> t.getRiskLevel() == Transaction.RiskLevel.HIGH
                || t.getRiskLevel() == Transaction.RiskLevel.CRITICAL).count();
        double amountAtRisk = all.stream()
                .filter(t -> t.getRiskLevel() == Transaction.RiskLevel.HIGH || t.getRiskLevel() == Transaction.RiskLevel.CRITICAL)
                .mapToDouble(Transaction::getAmount).sum();

        return ResponseEntity.ok(Map.of(
                "totalTransactions", total,
                "flaggedTransactions", flagged,
                "highRiskTransactions", highRisk,
                "amountAtRisk", amountAtRisk,
                "totalUsers", userRepository.count()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> users() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
