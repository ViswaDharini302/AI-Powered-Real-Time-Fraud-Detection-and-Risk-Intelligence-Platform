package com.frauddetectionsystem.controller;

import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final TransactionRepository transactionRepository;

    /**
     * Fraud/high-risk transaction count grouped by calendar day.
     * Feeds the "Fraud Trend" line chart on the dashboard.
     */
    @GetMapping("/fraud-trends")
    public ResponseEntity<List<Map<String, Object>>> fraudTrends() {
        List<Transaction> all = transactionRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> byDay = all.stream()
                .filter(t -> t.getRiskLevel() == Transaction.RiskLevel.HIGH
                        || t.getRiskLevel() == Transaction.RiskLevel.CRITICAL)
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().format(fmt),
                        TreeMap::new,
                        Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        byDay.forEach((day, count) -> result.add(Map.of("date", day, "flaggedCount", count)));
        return ResponseEntity.ok(result);
    }

    /**
     * How many transactions fall into each risk level bucket.
     * Feeds the "Risk Distribution" pie/bar chart.
     */
    @GetMapping("/risk-distribution")
    public ResponseEntity<Map<String, Long>> riskDistribution() {
        List<Transaction> all = transactionRepository.findAll();

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Transaction.RiskLevel level : Transaction.RiskLevel.values()) {
            long count = all.stream().filter(t -> t.getRiskLevel() == level).count();
            distribution.put(level.name(), count);
        }
        return ResponseEntity.ok(distribution);
    }

    /**
     * Transaction volume (count) grouped by calendar day.
     * Feeds the "Transaction Volume" bar chart.
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Map<String, Object>>> transactionVolume() {
        List<Transaction> all = transactionRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> byDay = all.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().format(fmt),
                        TreeMap::new,
                        Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        byDay.forEach((day, count) -> result.add(Map.of("date", day, "count", count)));
        return ResponseEntity.ok(result);
    }

    /**
     * Fraud/high-risk transaction count grouped by location.
     * Feeds the "Fraud by Location" chart.
     */
    @GetMapping("/fraud-by-location")
    public ResponseEntity<Map<String, Long>> fraudByLocation() {
        List<Transaction> all = transactionRepository.findAll();

        Map<String, Long> byLocation = all.stream()
                .filter(t -> t.getRiskLevel() == Transaction.RiskLevel.HIGH
                        || t.getRiskLevel() == Transaction.RiskLevel.CRITICAL)
                .collect(Collectors.groupingBy(Transaction::getLocation, Collectors.counting()));

        return ResponseEntity.ok(byLocation);
    }
}