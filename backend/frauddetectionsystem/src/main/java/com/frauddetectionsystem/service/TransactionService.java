package com.frauddetectionsystem.service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.frauddetectionsystem.dto.MlPredictionRequest;
import com.frauddetectionsystem.dto.RiskResponse;
import com.frauddetectionsystem.dto.TransactionRequest;
import com.frauddetectionsystem.entity.FraudAlert;
import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.entity.User;
import com.frauddetectionsystem.repository.FraudAlertRepository;
import com.frauddetectionsystem.repository.TransactionRepository;
import com.frauddetectionsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final FraudDetectionService fraudDetectionService;
    private final MlClientService mlClientService;
    private final RiskScoreService riskScoreService;
    public RiskResponse processTransaction(String userEmail, TransactionRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Transaction tx = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .location(request.getLocation())
                .deviceId(request.getDeviceId())
                .timestamp(LocalDateTime.now())
                .build();

        // 1) Rule engine
        FraudDetectionService.RuleResult ruleResult = fraudDetectionService.evaluate(user, tx);

        // 2) ML microservice
        MlPredictionRequest mlRequest = new MlPredictionRequest(
                tx.getAmount(),
                ruleResult.recentTransactionCount,
                ruleResult.newDevice ? 1 : 0,
                ruleResult.newLocation ? 1 : 0,
                tx.getTimestamp().getHour(),
                ruleResult.amountDeviation
        );
        double fraudProbability = mlClientService.getFraudProbability(mlRequest);

        // 3) Combine into final risk score
        int finalScore = riskScoreService.combine(ruleResult.score, fraudProbability);
        Transaction.RiskLevel level = riskScoreService.classify(finalScore);
        String reasonText = riskScoreService.buildReasonText(ruleResult.reasons, fraudProbability);

        tx.setRuleScore(ruleResult.score);
        tx.setFraudProbability(fraudProbability);
        tx.setRiskScore(finalScore);
        tx.setRiskLevel(level);
        tx.setRiskReasons(reasonText);
        tx.setStatus(finalScore >= 70
                ? Transaction.TransactionStatus.FLAGGED
                : Transaction.TransactionStatus.APPROVED);

        transactionRepository.save(tx);

        // 4) Create a fraud alert for high-risk transactions
        if (finalScore >= 70) {
            FraudAlert alert = FraudAlert.builder()
                    .transaction(tx)
                    .riskLevel(level)
                    .reason(reasonText)
                    .build();
            fraudAlertRepository.save(alert);
        }

        return new RiskResponse(tx.getId(), finalScore, level.name(), fraudProbability,
                Arrays.asList(reasonText.split("\n")));
    }

    public List<Transaction> getUserTransactions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    public Transaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }
}
