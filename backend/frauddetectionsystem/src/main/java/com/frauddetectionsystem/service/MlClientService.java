package com.frauddetectionsystem.service;

import com.frauddetectionsystem.dto.MlPredictionRequest;
import com.frauddetectionsystem.dto.MlPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
@Service
public class MlClientService {

    private final RestClient restClient;

    public MlClientService(@Value("${ml.service.url}") String mlServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(mlServiceUrl).build();
    }

    public double getFraudProbability(MlPredictionRequest request) {
        try {
            MlPredictionResponse response = restClient.post()
                    .body(request)
                    .retrieve()
                    .body(MlPredictionResponse.class);
            return response == null ? 0.0 : response.getFraud_probability();
        } catch (Exception e) {
            // If the ML service is down, don't crash the whole transaction flow -
            // fall back to rule-based scoring only, and log it.
            System.err.println("ML service call failed, falling back to rules only: " + e.getMessage());
            return 0.0;
        }
    }
}
