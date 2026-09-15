package com.frauddetectionsystem.service;
import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.entity.User;
import com.frauddetectionsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RULE-BASED fraud engine.
 * Each rule looks at ONE suspicious pattern and adds points to a running
 * score if it fires. This is intentionally simple and readable so you can
 * explain every rule in an interview.
 */
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;

    public static class RuleResult {
        public int score = 0;
        public List<String> reasons = new ArrayList<>();
        public boolean newDevice = false;
        public boolean newLocation = false;
        public double amountDeviation = 0;
        public int recentTransactionCount = 0;
    }

    public RuleResult evaluate(User user, Transaction tx) {
        RuleResult result = new RuleResult();

        List<Transaction> history = transactionRepository.findByUserOrderByTimestampDesc(user);

        // ---- Rule 1: High amount vs the user's own average ----
        double averageAmount = history.stream().mapToDouble(Transaction::getAmount).average().orElse(tx.getAmount());
        double deviation = averageAmount == 0 ? 0 : tx.getAmount() / averageAmount;
        result.amountDeviation = deviation;

        if (!history.isEmpty() && tx.getAmount() > averageAmount * 5) {
            result.score += 20;
            result.reasons.add("Transaction amount is significantly above your normal average");
        }

        // ---- Rule 2: New device ----
        boolean seenDevice = history.stream().anyMatch(t -> tx.getDeviceId().equals(t.getDeviceId()));
        result.newDevice = !seenDevice;
        if (!seenDevice && !history.isEmpty()) {
            result.score += 15;
            result.reasons.add("New device detected");
        }

        // ---- Rule 3: Unusual location ----
        boolean seenLocation = history.stream().anyMatch(t -> tx.getLocation().equalsIgnoreCase(t.getLocation()));
        result.newLocation = !seenLocation;
        if (!seenLocation && !history.isEmpty()) {
            result.score += 20;
            result.reasons.add("Unusual transaction location");
        }

        // ---- Rule 4: Unusual time (midnight - 5am) ----
        int hour = tx.getTimestamp().getHour();
        if (hour >= 0 && hour < 5) {
            result.score += 10;
            result.reasons.add("Transaction occurred during unusual hours");
        }

        // ---- Rule 5: Rapid successive transactions (5+ in last 5 minutes) ----
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        long recentCount = transactionRepository.findByUserAndTimestampAfter(user, fiveMinutesAgo).size();
        result.recentTransactionCount = (int) recentCount;
        if (recentCount >= 5) {
            result.score += 20;
            result.reasons.add("Multiple rapid transactions detected");
        }

        // cap at 100
        result.score = Math.min(result.score, 100);
        return result;
    }
}