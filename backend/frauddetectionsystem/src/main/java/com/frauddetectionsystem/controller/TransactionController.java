package com.frauddetectionsystem.controller;


import com.frauddetectionsystem.dto.RiskResponse;
import com.frauddetectionsystem.dto.TransactionRequest;
import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<RiskResponse> create(@Valid @RequestBody TransactionRequest request,
                                                Authentication auth) {
        return ResponseEntity.ok(transactionService.processTransaction(auth.getName(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Transaction>> myTransactions(Authentication auth) {
        return ResponseEntity.ok(transactionService.getUserTransactions(auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> all() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> byId(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    /**
     * Full investigation view: the flagged transaction PLUS the customer's
     * other transactions, so a fraud analyst can see the full picture in one
     * call (Feature 9 - Fraud Investigation).
     */
    @GetMapping("/{id}/investigate")
    public ResponseEntity<Map<String, Object>> investigate(@PathVariable Long id) {
        Transaction tx = transactionService.getById(id);
        List<Transaction> customerHistory = transactionService.getUserTransactions(tx.getUser().getEmail());

        long previousFraudCount = customerHistory.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.CONFIRMED_FRAUD)
                .count();

        return ResponseEntity.ok(Map.of(
                "transaction", tx,
                "customer", Map.of(
                        "name", tx.getUser().getName(),
                        "email", tx.getUser().getEmail()
                ),
                "customerHistory", customerHistory,
                "previousFraudCount", previousFraudCount
        ));
    }
}