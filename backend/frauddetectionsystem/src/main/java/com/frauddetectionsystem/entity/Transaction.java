package com.frauddetectionsystem.entity;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;

    private String transactionType; // ONLINE, ATM, POS, TRANSFER

    private String location;

    private String deviceId;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ---- Fraud analysis results (filled in AFTER the risk engine runs) ----
    private Integer ruleScore;
    private Double fraudProbability; // from the Python ML service, 0.0 - 1.0
    private Integer riskScore;       // final combined score, 0 - 100

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(length = 1000)
    private String riskReasons; // human-readable explanation, comma separated

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING_REVIEW;

    public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }
    public enum TransactionStatus { APPROVED, PENDING_REVIEW, FLAGGED, CONFIRMED_FRAUD, CONFIRMED_LEGITIMATE }
}