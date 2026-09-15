package com.frauddetectionsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.frauddetectionsystem.entity.FraudAlert;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findAllByOrderByCreatedAtDesc();
    List<FraudAlert> findByStatus(String status);
}