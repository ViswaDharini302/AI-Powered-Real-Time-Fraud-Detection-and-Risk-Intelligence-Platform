package com.frauddetectionsystem.service;

import com.frauddetectionsystem.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskScoreService {

    public int combine(int ruleScore, double fraudProbability) {
        double mlScore = fraudProbability * 100;
        double finalScore = (ruleScore * 0.5) + (mlScore * 0.5);
        return (int) Math.round(Math.min(finalScore, 100));
    }

    public Transaction.RiskLevel classify(int score) {
        if (score <= 30) return Transaction.RiskLevel.LOW;
        if (score <= 60) return Transaction.RiskLevel.MEDIUM;
        if (score <= 80) return Transaction.RiskLevel.HIGH;
        return Transaction.RiskLevel.CRITICAL;
    }

    public String buildReasonText(List<String> reasons, double fraudProbability) {
        StringBuilder sb = new StringBuilder();
        for (String r : reasons) {
            sb.append("- ").append(r).append("\n");
        }
        sb.append(String.format("- ML fraud probability: %.0f%%", fraudProbability * 100));
        return sb.toString();
    }
}
