package com.frauddetectionsystem.dto;

import lombok.Data;

@Data
public class MlPredictionResponse {
    private double fraud_probability;
}
