package com.frauddetectionsystem.service;
import com.frauddetectionsystem.entity.FraudAlert;
import com.frauddetectionsystem.entity.Transaction;
import com.frauddetectionsystem.repository.FraudAlertRepository;
import com.frauddetectionsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionRepository transactionRepository;

    public List<FraudAlert> getAllAlerts() {
        return fraudAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    public FraudAlert getAlert(Long id) {
        return fraudAlertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));
    }

    public FraudAlert review(Long alertId, String decision, String reviewerEmail) {
        FraudAlert alert = getAlert(alertId);
        Transaction tx = alert.getTransaction();

        if ("FRAUD".equalsIgnoreCase(decision)) {
            tx.setStatus(Transaction.TransactionStatus.CONFIRMED_FRAUD);
        } else if ("LEGITIMATE".equalsIgnoreCase(decision)) {
            tx.setStatus(Transaction.TransactionStatus.CONFIRMED_LEGITIMATE);
        } else {
            throw new IllegalArgumentException("decision must be FRAUD or LEGITIMATE");
        }

        transactionRepository.save(tx);
        alert.setStatus("REVIEWED");
        alert.setReviewedBy(reviewerEmail);
        return fraudAlertRepository.save(alert);
    }
}