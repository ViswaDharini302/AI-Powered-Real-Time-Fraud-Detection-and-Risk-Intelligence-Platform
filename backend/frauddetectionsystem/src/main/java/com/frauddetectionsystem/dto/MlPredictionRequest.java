package com.frauddetectionsystem.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MlPredictionRequest {
    private double amount;
    private int transaction_frequency;
    private int is_new_device;
    private int is_new_location;
    private int hour;
    private double amount_deviation;
}