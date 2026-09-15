package com.frauddetectionsystem.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskResponse {
    private Long transactionId;
    private Integer riskScore;
    private String riskLevel;
    private Double fraudProbability;
    private List<String> reasons;
}