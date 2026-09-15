package com.frauddetectionsystem.service;
import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.entity.User;
import com.frauddetectionsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        double averageAmount = history.stream().mapToDouble(Transaction::getAmount).average().orElse(tx.getAmount());
        double deviation = averageAmount == 0 ? 0 : tx.getAmount() / averageAmount;
        result.amountDeviation = deviation;

        if (!history.isEmpty() && tx.getAmount() > averageAmount * 5) {
            result.score += 20;
            result.reasons.add("Transaction amount is significantly above your normal average");
        }
        
        boolean seenDevice = history.stream().anyMatch(t -> tx.getDeviceId().equals(t.getDeviceId()));
        result.newDevice = !seenDevice;
        if (!seenDevice && !history.isEmpty()) {
            result.score += 15;
            result.reasons.add("New device detected");
        }
  
        boolean seenLocation = history.stream().anyMatch(t -> tx.getLocation().equalsIgnoreCase(t.getLocation()));
        result.newLocation = !seenLocation;
        if (!seenLocation && !history.isEmpty()) {
            result.score += 20;
            result.reasons.add("Unusual transaction location");
        }
        int hour = tx.getTimestamp().getHour();
        if (hour >= 0 && hour < 5) {
            result.score += 10;
            result.reasons.add("Transaction occurred during unusual hours");
        }
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        long recentCount = transactionRepository.findByUserAndTimestampAfter(user, fiveMinutesAgo).size();
        result.recentTransactionCount = (int) recentCount;
        if (recentCount >= 5) {
            result.score += 20;
            result.reasons.add("Multiple rapid transactions detected");
        }
        result.score = Math.min(result.score, 100);
        return result;
    }
}
